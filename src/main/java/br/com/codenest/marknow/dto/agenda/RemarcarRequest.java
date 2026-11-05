package br.com.codenest.marknow.dto.agenda;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

public record RemarcarRequest(
        @Schema(description = "Opcional: troca o profissional") String oidProfissional,
        @NotNull @Schema(description = "Novo horário local (America/Sao_Paulo)", example = "2026-11-10T14:30")
        LocalDateTime inicio
) {
}
