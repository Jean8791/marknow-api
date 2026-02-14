package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "assinatura")
public class Assinatura extends AbstractEntity {

    @Column(name = "oid_prestador", length = 36, nullable = false, unique = true)
    public String oidPrestador;

    @Column(nullable = false, length = 30)
    public String plano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public AssinaturaStatus status;

    @Column(name = "fim_periodo_atual", nullable = false)
    public Instant fimPeriodoAtual;

    @Column(name = "tolerancia_ate")
    public Instant toleranciaAte;

    @Column(name = "provedor", length = 20)
    public String provedor;

    @Column(name = "referencia_provedor", length = 80)
    public String referenciaProvedor;
}
