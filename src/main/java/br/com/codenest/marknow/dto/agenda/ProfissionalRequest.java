package br.com.codenest.marknow.dto.agenda;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProfissionalRequest(
        @NotBlank @Size(min = 2, max = 120) String nome,
        @Pattern(regexp = "^$|\\d{2}", message = "DDD inválido") String dddTelefone,
        @Pattern(regexp = "^$|\\d{8,9}", message = "Telefone inválido") String telefone,
        Boolean ativo,
        List<@Valid DisponibilidadeDTO> disponibilidades
) {
}
