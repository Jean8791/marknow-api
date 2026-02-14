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

        // TRIAL e ATIVA valem, desde que dentro do período (ou grace)
        boolean dentroPeriodo = !a.fimPeriodoAtual.isBefore(now);
        if (dentroPeriodo) return a.status == AssinaturaStatus.TRIAL || a.status == AssinaturaStatus.ATIVA;

        // grace (se existir)
        //if (a.graceUntil != null && !a.graceUntil.isBefore(now)) return true;

        return false;
    }
}
