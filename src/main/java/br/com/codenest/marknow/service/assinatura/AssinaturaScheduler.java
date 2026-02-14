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

            Prestador p = Prestador.findById(a.oidPrestador);
            if (p == null) continue;

            if (!ativa) {
                // marca inadimplente se ainda não estiver
                if (a.status != AssinaturaStatus.INADIMPLENTE && a.status != AssinaturaStatus.CANCELADA) {
                    a.status = AssinaturaStatus.INADIMPLENTE;
                    a.atualizadoEm = now;
                }
                // bloqueia prestador
                if (p.status != PrestadorStatus.BLOQUEADO) {
                    p.status = PrestadorStatus.BLOQUEADO;
                    p.atualizadoEm = now;
                }
            } else {
                // assinatura ok -> prestador ativo
                if (p.status == PrestadorStatus.BLOQUEADO) {
                    p.status = PrestadorStatus.ATIVO;
                    p.atualizadoEm = now;
                }
                // se estava INADIMPLENTE mas já está ativa, normaliza
                if (a.status == AssinaturaStatus.INADIMPLENTE) {
                    a.status = AssinaturaStatus.ATIVA;
                    a.atualizadoEm = now;
                }
            }
        }
    }
}