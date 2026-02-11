package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import br.com.codenest.marknow.enums.UserAccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_account", uniqueConstraints = @UniqueConstraint(name = "uk_user_account_email", columnNames = "email"))
public class UserAccount extends AbstractEntity {
    @Column(name = "email", nullable = false, length = 100)
    public String email;

    @Column(name = "full_name", nullable = false, length = 120)
    public String fullName;

    @Column(name = "password_hash")
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public UserAccountStatus status;

    @PrePersist
    public void beforePersist() {
        if (this.status == null) {
            this.status = UserAccountStatus.ACTIVE;
        }
    }
}
