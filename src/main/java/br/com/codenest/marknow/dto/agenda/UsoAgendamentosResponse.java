package br.com.codenest.marknow.dto.agenda;

public record UsoAgendamentosResponse(
        long usados,
        Integer limite,
        int percentual,
        boolean alerta,
        boolean bloqueado
) {
}
