package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.enums.TipoPessoa;
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

    @Column(name = "documento", length = 2)
    public String documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 2)
    public TipoPessoa tipoPessoa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public PrestadorStatus status;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = PrestadorStatus.ATIVO;
        }
    }
}
