package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.util.UtilsDocumento;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepositoryBase<Usuario, String> {

    public List<Usuario> findAll(int page, int size, String sort) {
        Sort panacheSort = parseSort(sort);
        return findAll(panacheSort).page(page, size).list();
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    public boolean existsByEmailEmOutroRegistro(String oidUsuario, String novoEmail) {
        String novo = UtilsDocumento.apenasDigitosParaNull(novoEmail);
        if (novo == null) return false;

        return count("email = ?1 and oid <> ?2", novo, oidUsuario) > 0;
    }

    private Sort parseSort(String sort) {
        Sort s = Sort.by("createdAt").descending();

        if (sort == null || sort.trim().isEmpty()) return s;

        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();
        String direction = (parts.length > 1 ? parts[1].trim() : "asc");

        if (!field.equals("email") && !field.equals("nome") && !field.equals("createdAt") && !field.equals("updatedAt") && !field.equals("status")) {
            return s;
        }

        s = Sort.by(field);
        return "desc".equalsIgnoreCase(direction) ? s.descending() : s.ascending();
    }

}
