package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Plano;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@ApplicationScoped
public class PlanoRepository implements PanacheRepositoryBase<Plano, String> {

    public Plano findByOid(@NotBlank String oid) {
        return findById(oid);
    }

    public List<Plano> findAll(int page, int size, String sort) {
        Sort panacheSort = Sort.by(sort);
        return findAll(panacheSort).page(page, size).list();
    }

}
