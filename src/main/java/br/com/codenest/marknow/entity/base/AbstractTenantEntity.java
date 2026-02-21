package br.com.codenest.marknow.entity.base;

import br.com.codenest.marknow.service.auditoria.AuditoriaService;
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
    void preencherAuditoria() {
        AuditoriaService auditoria = io.quarkus.arc.Arc.container().instance(AuditoriaService.class).get();

        if (this.oidPrestador == null) {
            this.oidPrestador = auditoria.oidPrestadorLogadoOrThrow();
        }

        if (this.criadoPor == null) {
            this.criadoPor = auditoria.oidUsuarioLogadoOrThrow();
        }
    }
}

