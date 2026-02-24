package br.com.codenest.marknow.dto.plano;

import br.com.codenest.marknow.util.UtilsString;
import br.com.codenest.marknow.util.UtilsValor;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PlanoCreateRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    private String nome;

    @NotNull(message = "Informe o limite de agendamentos")
    private Integer limiteAgendamentos;

    @NotNull(message = "Informe o valor do plano")
    private BigDecimal valor;

    private Boolean possuiTrial;

    @Min(1)
    @NotNull
    private Integer diasTrial;

    @JsonSetter("nome")
    public void setNome(String nome) {
        this.nome = UtilsString.apararParaNull(nome);
    }

    @JsonSetter("limiteAgendamentos")
    public void setLimiteAgendamentos(Integer limiteAgendamentos) {
        this.limiteAgendamentos = UtilsValor.padraoZero(limiteAgendamentos) <= 0 ? null : limiteAgendamentos;
    }

    @JsonSetter("valor")
    public void setValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            this.valor = null;
        } else {
            this.valor = valor;
        }
    }

    @JsonSetter("possuiTrial")
    public void setPossuiTrial(Boolean possuiTrial) {
        this.possuiTrial = possuiTrial != null && possuiTrial;
    }

    @JsonSetter("diasTrial")
    public void setDiasTrial(Integer diasTrial) {
        this.diasTrial = diasTrial;
    }
}
