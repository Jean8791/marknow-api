package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.PlanoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "plano")
public class Plano extends AbstractEntity {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "limite_agendamentos", nullable = false)
    private Integer limiteAgendamentos;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PlanoStatus status;

    @Column(name = "possui_trial")
    private Boolean possuiTrial;

    @Column(name = "dias_trial")
    private Integer diasTrial;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = PlanoStatus.INATIVO;
        }
        if (this.possuiTrial == null) {
            this.possuiTrial = false;
        }
        if (this.diasTrial == null) {
            this.diasTrial = 0;
        }
    }
}
