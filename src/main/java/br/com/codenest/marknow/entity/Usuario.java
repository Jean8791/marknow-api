package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.enums.UsuarioStatus;
import br.com.codenest.marknow.util.UtilsTelefone;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = {"email"}))
public class Usuario extends AbstractEntity {

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "documento", length = 14)
    private String documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 2)
    private TipoPessoa tipoPessoa;

    @Column(name = "ddd_telefone", length = 5)
    private String dddTelefone;

    @Column(name = "telefone", length = 30)
    private String telefone;

    @Column(name = "ddd_celular", length = 5)
    private String dddCelular;

    @Column(name = "celular", length = 30)
    private String celular;

    @Column(name = "imagem", columnDefinition = "TEXT")
    private String imagem;

    @Column(name = "senha")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UsuarioStatus status;

    @Column(name = "admin")
    private Boolean admin;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = UsuarioStatus.ATIVO;
        }
        this.dddTelefone = UtilsTelefone.apenasNumeros(this.dddTelefone);
        this.telefone = UtilsTelefone.apenasNumeros(this.telefone);
        this.dddCelular = UtilsTelefone.apenasNumeros(this.dddCelular);
        this.celular = UtilsTelefone.apenasNumeros(this.celular);
    }
}
