package br.com.codenest.marknow.dto.agenda;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServicoRequest(
        @NotBlank @Size(min = 2, max = 120) String nome,
        @NotNull(message = "Informe a duração") Integer duracaoMinutos,
        @DecimalMin(value = "0", message = "Valor inválido") BigDecimal valor,
        Boolean ativo
) {
}
