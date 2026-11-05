package br.com.codenest.marknow.dto.agenda;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalTime;

public record DisponibilidadeDTO(
        @NotNull @Min(1) @Max(7)
        @Schema(description = "1 = segunda ... 7 = domingo", example = "1")
        Integer diaSemana,
        @NotNull @Schema(type = org.eclipse.microprofile.openapi.annotations.enums.SchemaType.STRING, example = "08:00")
        LocalTime horaInicio,
        @NotNull @Schema(type = org.eclipse.microprofile.openapi.annotations.enums.SchemaType.STRING, example = "12:00")
        LocalTime horaFim
) {
}
