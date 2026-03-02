package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import br.com.codenest.marknow.util.UtilsDocumento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepositoryBase<Usuario, String> {

    @Inject
    AuditoriaService auditoriaService;

    public PanacheQuery<Usuario> findAll(int page, int size, String sort) {
        return find(
                "select u from Usuario u join PrestadorUsuario pu on pu.usuario = u where pu.prestador.oid = ?1",
                Sort.by(sort).descending(),
                auditoriaService.oidPrestadorLogadoOrThrow()
        ).page(page, size);
    }

    public boolean existsByEmail(String email) {
        return count("select u from Usuario u join PrestadorUsuario pu on pu.usuario = u where pu.prestador.oid = ?1 and u.email = ?2",
                auditoriaService.oidPrestadorLogadoOrThrow(),
                email) > 0;
    }

    public boolean existsByEmailEmOutroRegistro(String oidUsuario, String novoEmail) {
        String novo = UtilsDocumento.apenasDigitosParaNull(novoEmail);
        if (novo == null) return false;

        return count("select u from Usuario u join PrestadorUsuario pu on pu.usuario = u where pu.prestador.oid = ?1 and u.email = ?2 and u.oid <> ?3",
                auditoriaService.oidPrestadorLogadoOrThrow(),
                novo,
                oidUsuario) > 0;
    }

}
