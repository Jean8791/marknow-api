package br.com.codenest.marknow.dto.agenda;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

public record AgendamentoRequest(
        @NotBlank String oidProfissional,
        @NotBlank String oidServico,
        @NotNull @Schema(description = "Horário local (America/Sao_Paulo)", example = "2026-11-10T09:00")
        LocalDateTime inicio,
        @NotBlank @Size(min = 2, max = 120) String clienteNome,
        @NotBlank @Pattern(regexp = "\\d{2}", message = "DDD do cliente inválido") String clienteDdd,
        @NotBlank @Pattern(regexp = "\\d{8,9}", message = "Telefone do cliente inválido") String clienteTelefone,
        @Size(max = 500) String observacao
) {
}
