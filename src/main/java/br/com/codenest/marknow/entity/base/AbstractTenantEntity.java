package br.com.codenest.marknow.entity.base;

import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@FilterDef(
        name = "tenantFilter",
        parameters = @ParamDef(name = "oidPrestador", type = String.class)
)
@Filter(name = "tenantFilter", condition = "oid_prestador = :oidPrestador")
public abstract class AbstractTenantEntity extends AbstractEntity {

    @Column(name = "oid_prestador", nullable = false, length = 36)
    protected String oidPrestador;

    @ManyToOne
    @JoinColumn(name = "oid_prestador", insertable = false, updatable = false)
    private Prestador prestador;

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

