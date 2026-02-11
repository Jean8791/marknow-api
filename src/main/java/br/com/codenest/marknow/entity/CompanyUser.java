package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.CompanyUserRole;
import br.com.codenest.marknow.enums.CompanyUserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "company_user", uniqueConstraints = @UniqueConstraint(name = "uk_company_user_company_user", columnNames = {"company_oid", "user_oid"}))
public class CompanyUser extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_oid", nullable = false)
    public Company company;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_oid", nullable = false)
    public UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    public CompanyUserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public CompanyUserStatus status;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = CompanyUserStatus.ACTIVE;
        }
    }

}
