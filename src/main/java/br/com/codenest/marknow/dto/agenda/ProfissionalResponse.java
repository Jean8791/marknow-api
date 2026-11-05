package br.com.codenest.marknow.dto.agenda;

import java.util.List;

public record ProfissionalResponse(
        String oid,
        String nome,
        String dddTelefone,
        String telefone,
        Boolean ativo,
        List<DisponibilidadeDTO> disponibilidades
) {
}
