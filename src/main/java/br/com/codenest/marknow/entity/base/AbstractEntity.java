package br.com.codenest.marknow.entity.base;

import br.com.codenest.marknow.util.UtilsRandom;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractEntity extends PanacheEntityBase implements Serializable {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    protected String oid;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (oid == null) oid = UtilsRandom.uuid();
        if (criadoEm == null) criadoEm = now;
    }

    @PreUpdate
    void onUpdate() {
        atualizadoEm = Instant.now();
    }
}
