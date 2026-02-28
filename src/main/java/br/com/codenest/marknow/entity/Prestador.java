package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.util.UtilsTelefone;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "prestador")
public class Prestador extends AbstractEntity {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "documento", length = 14)
    private String documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 2)
    private TipoPessoa tipoPessoa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PrestadorStatus status;

    @Column(name = "email", length = 120)
    private String email    ;

    @Column(name = "ddd_telefone", length = 5)
    private String dddTelefone;

    @Column(name = "telefone", length = 30)
    private String telefone;

    @Column(name = "ddd_celular", length = 5)
    private String dddCelular;

    @Column(name = "celular", length = 30)
    private String celular;

    @Column(name = "cep", length = 9)
    private String cep;

    @Column(name = "logradouro", length = 150)
    private String logradouro;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid", referencedColumnName = "oid_prestador", insertable = false, updatable = false)
    private Assinatura assinatura;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = PrestadorStatus.BLOQUEADO;
        }
        this.dddTelefone = UtilsTelefone.apenasNumeros(this.dddTelefone);
        this.telefone = UtilsTelefone.apenasNumeros(this.telefone);
        this.dddCelular = UtilsTelefone.apenasNumeros(this.dddCelular);
        this.celular = UtilsTelefone.apenasNumeros(this.celular);
    }
}