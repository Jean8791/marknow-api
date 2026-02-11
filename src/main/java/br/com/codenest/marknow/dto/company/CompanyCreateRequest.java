package br.com.codenest.marknow.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "CompanyCreateRequest")
public class CompanyCreateRequest {

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(required = true, example = "Clínica Exemplo LTDA")
    public String name;

    @Pattern(regexp = "^\\d{14}$", message = "cnpj deve conter 14 dígitos (somente números)")
    @Schema(example = "12345678000199")
    public String cnpj;
}
