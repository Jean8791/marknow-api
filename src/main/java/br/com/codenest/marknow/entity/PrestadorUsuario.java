package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.enums.PrestadorUsuarioStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "prestador_usuario", uniqueConstraints = @UniqueConstraint(name = "uk_prestador_usuario", columnNames = {"oid_prestador", "oid_usuario"}))
public class PrestadorUsuario extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_prestador", nullable = false)
    public Prestador prestador;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_usuario", nullable = false)
    public Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    public PrestadorUsuarioRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public PrestadorUsuarioStatus status;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = PrestadorUsuarioStatus.ATIVO;
        }
    }
}
