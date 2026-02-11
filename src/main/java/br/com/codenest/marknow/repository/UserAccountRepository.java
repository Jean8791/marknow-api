package br.com.codenest.marknow.repository;


import br.com.codenest.marknow.entity.UserAccount;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserAccountRepository implements PanacheRepositoryBase<UserAccount, String> {

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}
