package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "profissional")
public class Profissional extends AbstractTenantEntity {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "ddd_telefone", length = 2)
    private String dddTelefone;

    @Column(name = "telefone", length = 9)
    private String telefone;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @PrePersist
    void beforePersist() {
        if (ativo == null) ativo = true;
    }
}
