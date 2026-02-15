package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractTenantEntity;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "assinatura")
public class Assinatura extends AbstractTenantEntity {

    @Column(nullable = false, length = 30)
    private String plano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssinaturaStatus status;

    @Column(name = "fim_periodo_atual", nullable = false)
    private Instant fimPeriodoAtual;

    @Column(name = "tolerancia_ate")
    private Instant toleranciaAte;

    @Column(name = "provedor", length = 20)
    private String provedor;

    @Column(name = "referencia_provedor", length = 80)
    private String referenciaProvedor;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = AssinaturaStatus.TRIAL;
        }
    }
}
