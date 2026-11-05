package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import br.com.codenest.marknow.util.PaginationUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepositoryBase<Usuario, String> {

    @Inject
    AuditoriaService auditoriaService;

    public PanacheQuery<Usuario> findAll(int page, int size, String sort, String search) {
        String termo = search == null || search.isBlank() ? "" : search.trim().toLowerCase();
        return find(
                "select u from Usuario u join PrestadorUsuario pu on pu.usuario = u where pu.prestador.oid = ?1"
                        + " and (?2 = '' or lower(u.nome) like concat('%', ?2, '%') or lower(u.email) like concat('%', ?2, '%') or u.documento like concat('%', ?2, '%'))",
                Sort.by("u." + PaginationUtil.campoOrdenacao(sort, Set.of("nome", "email", "criadoEm"), "criadoEm")),
                auditoriaService.oidPrestadorLogadoOrThrow(),
                termo
        ).page(page, size);
    }

    public Usuario findByOidNoPrestador(String oidUsuario, String oidPrestador) {
        return find("select u from Usuario u join PrestadorUsuario pu on pu.usuario = u where u.oid = ?1 and pu.prestador.oid = ?2",
                oidUsuario,
                oidPrestador).firstResult();
    }

    public Usuario findByEmail(String email) {
        if (email == null) return null;
        return find("lower(email) = ?1", email.trim().toLowerCase()).firstResult();
    }

    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return count("lower(email) = ?1", email.trim().toLowerCase()) > 0;
    }

    public boolean existsByEmailEmOutroRegistro(String oidUsuario, String novoEmail) {
        if (novoEmail == null || novoEmail.isBlank()) return false;
        return count("lower(email) = ?1 and oid <> ?2", novoEmail.trim().toLowerCase(), oidUsuario) > 0;
    }

}
