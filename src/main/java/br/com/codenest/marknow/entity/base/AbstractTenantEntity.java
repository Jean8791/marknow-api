package br.com.codenest.marknow.entity.base;

import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractTenantEntity extends AbstractEntity {

    @Column(name = "oid_prestador", nullable = false, length = 36)
    protected String oidPrestador;

    @Column(name = "criado_por", nullable = false, length = 36)
    private String criadoPor;

    @Column(name = "atualizado_por", length = 36)
    private String atualizadoPor;

    @PrePersist
    void validateTenant() {
        if (oidPrestador == null || oidPrestador.isBlank()) {
            throw new IllegalStateException("oidPrestador é obrigatório.");
        }
    }
}

