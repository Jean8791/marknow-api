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
        Sort panacheSort = parseSort(sort);
        return findAll(panacheSort).page(page, size).list();
    }


    private Sort parseSort(String sort) {
        Sort s = Sort.by("createdAt").descending();

        if (sort == null || sort.trim().isEmpty()) return s;

        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();
        String direction = (parts.length > 1 ? parts[1].trim() : "asc");

        if (!field.equals("nome") && !field.equals("createdAt") && !field.equals("updatedAt") && !field.equals("status")) {
            return s;
        }

        s = Sort.by(field);
        return "desc".equalsIgnoreCase(direction) ? s.descending() : s.ascending();
    }
}
