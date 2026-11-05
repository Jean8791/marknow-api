package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "disponibilidade")
public class Disponibilidade extends AbstractTenantEntity {

    @Column(name = "oid_profissional", nullable = false, length = 36)
    private String oidProfissional;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;
}
