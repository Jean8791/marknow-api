package br.com.codenest.marknow.service.assinatura;

import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;

@ApplicationScoped
public class AssinaturaPolitica {
    public boolean isAtiva(Assinatura a, Instant now) {
        if (a == null) return false;

        if (a.status == AssinaturaStatus.CANCELADA || a.status == AssinaturaStatus.SUSPENSA) return false;

        if (a.fimPeriodoAtual == null) return false;
        boolean dentroPeriodo = !a.fimPeriodoAtual.isBefore(now);
        if (dentroPeriodo) return a.status == AssinaturaStatus.TRIAL || a.status == AssinaturaStatus.ATIVA;

        // tolerancia
        return a.toleranciaAte != null && !a.toleranciaAte.isBefore(now);
    }
}
