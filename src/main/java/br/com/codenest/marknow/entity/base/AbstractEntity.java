package br.com.codenest.marknow.entity.base;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractEntity extends PanacheEntityBase implements Serializable {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    protected String oid;

    @Column(name = "created_by", length = 36)
    protected String createdBy;

    @Column(name = "updated_by", length = 36)
    protected String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    protected Instant updatedAt;

    @PrePersist
    public void beforePersist() {
        if (this.oid == null) {
            this.oid = UUID.randomUUID().toString();
        }
    }

}
