package br.com.codenest.marknow.dto.assinatura;

import br.com.codenest.marknow.entity.Plano;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class AssinaturaResponse {
    private String oid;
    private String plano;
    private AssinaturaStatus status;
    private Instant fimPeriodoAtual;
    private Instant toleranciaAte;
    private String provedor;
    private String referenciaProvedor;
    private String criadoPor;
    private String atualizadoPor;
    private Instant criadoEm;
    private Instant atualizadoEm;
}
