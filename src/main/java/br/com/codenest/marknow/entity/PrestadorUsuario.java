package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.enums.PrestadorUsuarioStatus;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "prestador_usuario",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_prestador_usuario",
                columnNames = {"oid_prestador", "oid_usuario"}
        )
)
public class PrestadorUsuario extends AbstractEntity {

    @Column(name = "oid_prestador", nullable = false, length = 36, updatable = false)
    private String oidPrestador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_prestador", insertable = false, updatable = false, nullable = false)
    private Prestador prestador;

    @Column(name = "oid_usuario", nullable = false, length = 36)
    private String oidUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_usuario", insertable = false, updatable = false, nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private PrestadorUsuarioRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PrestadorUsuarioStatus status;

    @PrePersist
    void beforePersist() {
        if (status == null) status = PrestadorUsuarioStatus.ATIVO;

        if (oidPrestador == null || oidPrestador.isBlank()) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "oidPrestador é obrigatório.");
        }
        if (oidUsuario == null || oidUsuario.isBlank()) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "oidUsuario é obrigatório.");
        }
        if (role == null) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "role é obrigatório.");
        }
    }
}
