package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.UsuarioStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"))
public class Usuario extends AbstractEntity {

    @Column(name = "email", nullable = false, length = 100)
    public String email;

    @Column(name = "nome", nullable = false, length = 120)
    public String nome;

    @Column(name = "senha")
    public String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public UsuarioStatus status;
}
