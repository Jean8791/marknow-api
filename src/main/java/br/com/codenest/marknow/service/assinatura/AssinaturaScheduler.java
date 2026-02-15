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
import java.util.List;

@ApplicationScoped
public class AssinaturaScheduler {

    @Inject
    AssinaturaPolitica politica;

    @Scheduled(every = "10m")
    @Transactional
    void verificarAssinaturas() {
        Instant now = Instant.now();

        List<Assinatura> assinaturas = Assinatura.listAll();

        for (Assinatura a : assinaturas) {
            boolean ativa = politica.isAtiva(a, now);

            Prestador p = Prestador.findById(a.getOidPrestador());
            if (p == null) continue;

            if (!ativa) {
                // marca inadimplente se ainda não estiver
                if (!AssinaturaStatus.INADIMPLENTE.equals(a.getStatus()) && !AssinaturaStatus.CANCELADA.equals(a.getStatus())) {
                    a.setStatus(AssinaturaStatus.INADIMPLENTE);
                    a.setAtualizadoEm(now);
                }
                // bloqueia prestador
                if (!PrestadorStatus.BLOQUEADO.equals(p.getStatus())) {
                    p.setStatus(PrestadorStatus.BLOQUEADO);
                    p.setAtualizadoEm(now);
                }
            } else {
                // assinatura ok -> prestador ativo
                if (PrestadorStatus.BLOQUEADO.equals(p.getStatus())) {
                    p.setStatus(PrestadorStatus.ATIVO);
                    p.setAtualizadoEm(now);
                }
                // se estava INADIMPLENTE mas já está ativa, normaliza
                if (AssinaturaStatus.INADIMPLENTE.equals(a.getStatus())) {
                    a.setStatus(AssinaturaStatus.ATIVA);
                    a.setAtualizadoEm(now);
                }
            }
        }
    }
}