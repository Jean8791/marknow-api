package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.CompanyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "company")
public class Company extends AbstractEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "cnpj", length = 14)
    public String cnpj;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public CompanyStatus status;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = CompanyStatus.ACTIVE;
        }
    }
}
