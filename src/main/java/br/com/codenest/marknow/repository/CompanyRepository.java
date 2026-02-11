package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Company;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CompanyRepository implements PanacheRepositoryBase<Company, String> {
    public boolean existsByCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) return false;
        return count("cnpj", cnpj) > 0;
    }
}
