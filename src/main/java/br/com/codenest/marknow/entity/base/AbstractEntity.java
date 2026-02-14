package br.com.codenest.marknow.entity.base;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractEntity extends PanacheEntityBase implements Serializable {

    @Id
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    protected String oid;

    @Column(name = "criado_por", length = 36)
    public String criadoPor;

    @Column(name = "atualizado_por", length = 36)
    public String atualizadoPor;

    @Column(name = "criado_em", nullable = false)
    public Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    public Instant atualizadoEm;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (criadoEm == null) criadoEm = now;
    }

    @PreUpdate
    void onUpdate() {
        atualizadoEm = Instant.now();
    }
}
