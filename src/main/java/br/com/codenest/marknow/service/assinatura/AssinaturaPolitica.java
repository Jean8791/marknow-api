package br.com.codenest.marknow.service.assinatura;

import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;

@ApplicationScoped
public class AssinaturaPolitica {
    public boolean isAtiva(Assinatura a, Instant now) {
        if (a == null) return false;

        if (AssinaturaStatus.CANCELADA.equals(a.getStatus()) || AssinaturaStatus.SUSPENSA.equals(a.getStatus())) {
            return false;
        }

        if (a.getFimPeriodoAtual() == null) return false;
        boolean dentroPeriodo = !a.getFimPeriodoAtual().isBefore(now);
        if (dentroPeriodo)
            return AssinaturaStatus.TRIAL.equals(a.getStatus()) || AssinaturaStatus.ATIVA.equals(a.getStatus());

        return a.getToleranciaAte() != null && !a.getToleranciaAte().isBefore(now);
    }
}
