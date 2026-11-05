package br.com.codenest.marknow.dto.agenda;

import java.math.BigDecimal;

public record ServicoResponse(
        String oid,
        String nome,
        Integer duracaoMinutos,
        BigDecimal valor,
        Boolean ativo
) {
}
