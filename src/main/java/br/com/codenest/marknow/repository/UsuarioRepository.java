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
        Sort panacheSort = Sort.by(sort);
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

}
