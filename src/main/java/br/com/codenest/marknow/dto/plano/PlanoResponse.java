package br.com.codenest.marknow.dto.plano;

import br.com.codenest.marknow.enums.PlanoStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PlanoResponse {
    private String oid;
    private String nome;
    private Integer limiteAgendamentos;
    private BigDecimal valor;
    private PlanoStatus status;
    private Boolean possuiTrial;
    private Integer diasTrial;
}
