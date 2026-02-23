package br.com.codenest.marknow.dto.convite;

import br.com.codenest.marknow.util.UtilsData;
import br.com.codenest.marknow.util.UtilsString;
import br.com.codenest.marknow.util.UtilsValor;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Future;
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

    @NotBlank(message = "Informme a descrição")
    private String descricao;

    private String emailDestinatario;

    @NotNull(message = "Informe a data de expiração")
    @Future(message = "A data de expiração deve ser no futuro")
    private Instant expiraEm;

    @NotNull(message = "Informe o maximo de uso")
    private Integer maxUsos;

    @JsonSetter("descricao")
    public void setDescricao(String descricao) {
        this.descricao = UtilsString.apararParaNull(descricao);
    }

    @JsonSetter("maxUsos")
    public void setMaxUsos(Integer maxUsos) {
        this.maxUsos = UtilsValor.padraoZero(maxUsos) <= 0 ? null : maxUsos;
    }
}
