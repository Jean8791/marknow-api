package br.com.codenest.marknow.service.assinatura;

import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import br.com.codenest.marknow.enums.PrestadorStatus;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class AssinaturaScheduler {

    @Inject
    AssinaturaPolitica politica;

    @Scheduled(every = "5m")
    @Transactional
    public void verificarAssinaturas() {
        Instant now = Instant.now();
        LocalDate hoje = politica.toLocalDate(now);

        List<Assinatura> assinaturas = Assinatura.listAll();

        for (Assinatura a : assinaturas) {
            if (a.getOidPrestador() == null) continue;

            Prestador p = Prestador.findById(a.getOidPrestador());
            if (p == null) continue;

            // 1. Verifica os prazos usando a Política
            boolean periodoVencido = politica.isPeriodoVencido(a, hoje);
            boolean toleranciaVencida = politica.isToleranciaVencida(a, hoje);

            // 2. Aplica a Regra de Status "Escada"
            if (periodoVencido) {
                if (toleranciaVencida) {
                    // ACABOU TUDO: Bloqueia acesso
                    a.setStatus(AssinaturaStatus.INADIMPLENTE);
                    p.setStatus(PrestadorStatus.BLOQUEADO);
                } else {
                    // ATRASADO: Mas ainda liberado (dentro da tolerância)
                    a.setStatus(AssinaturaStatus.ATRASADO);
                    p.setStatus(PrestadorStatus.ATIVO);
                }

                a.setAtualizadoEm(now);
                p.setAtualizadoEm(now);

            } else {
                // TUDO EM DIA: Normaliza se necessário
                if (AssinaturaStatus.ATRASADO.equals(a.getStatus()) || AssinaturaStatus.INADIMPLENTE.equals(a.getStatus())) {
                    a.setStatus(AssinaturaStatus.ATIVA);
                    a.setAtualizadoEm(now);
                }

                if (PrestadorStatus.BLOQUEADO.equals(p.getStatus())) {
                    p.setStatus(PrestadorStatus.ATIVO);
                    p.setAtualizadoEm(now);
                }
            }
        }
    }
}