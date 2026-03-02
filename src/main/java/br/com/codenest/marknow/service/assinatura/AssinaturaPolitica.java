package br.com.codenest.marknow.service.assinatura;

import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@ApplicationScoped
public class AssinaturaPolitica {

    public static final ZoneId ZONE_BR = ZoneId.of("America/Sao_Paulo");

    /**
     * Define se o prestador pode ou não usar o sistema.
     * Retorna true se estiver no prazo ou dentro da tolerância.
     */
    public boolean isAcessoLiberado(Assinatura a, Instant now) {
        if (a == null) return false;

        // Status que bloqueiam acesso imediatamente
        if (List.of(AssinaturaStatus.CANCELADA, AssinaturaStatus.SUSPENSA, AssinaturaStatus.INADIMPLENTE).contains(a.getStatus())) {
            return false;
        }

        LocalDate hoje = toLocalDate(now);

        // 1. Verificação do período principal
        if (!isPeriodoVencido(a, hoje)) {
            return true;
        }

        // 2. Verificação da tolerância
        return a.getToleranciaAte() != null && !isToleranciaVencida(a, hoje);
    }

    // Métodos auxiliares para centralizar a conversão de data
    public LocalDate toLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, ZONE_BR);
    }

    public boolean isPeriodoVencido(Assinatura a, LocalDate hoje) {
        if (a.getFimPeriodoAtual() == null) return true;
        return hoje.isAfter(toLocalDate(a.getFimPeriodoAtual()));
    }

    public boolean isToleranciaVencida(Assinatura a, LocalDate hoje) {
        if (a.getToleranciaAte() == null) return true;
        return hoje.isAfter(toLocalDate(a.getToleranciaAte()));
    }
}