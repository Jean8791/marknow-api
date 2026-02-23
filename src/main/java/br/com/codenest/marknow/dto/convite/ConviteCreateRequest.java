package br.com.codenest.marknow.dto.convite;

import br.com.codenest.marknow.util.UtilsString;
import br.com.codenest.marknow.util.UtilsValor;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class ConviteCreateRequest {

    @NotBlank(message = "Informme o código")
    private String codigo;

    private String emailDestinatario;

    @NotNull(message = "Informe a data de expiração")
    private Instant expiraEm;

    @NotNull(message = "Informe o maximo de uso")
    private Integer maxUsos;

    @JsonSetter("codigo")
    public void setCodigo(String codigo) {
        this.codigo = UtilsString.apararParaNull(codigo);
    }

    @JsonSetter("maxUsos")
    public void setMaxUsos(Integer maxUsos) {
        this.maxUsos = UtilsValor.padraoZero(maxUsos) <= 0 ? null : maxUsos;
    }
}
