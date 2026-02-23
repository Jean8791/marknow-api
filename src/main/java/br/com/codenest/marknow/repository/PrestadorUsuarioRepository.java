package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.PrestadorUsuario;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrestadorUsuarioRepository implements PanacheRepositoryBase<PrestadorUsuario, String> {
    public PrestadorUsuario findByOidPrestadorAndOidUsuario(String prestadorOid, String usuarioOid) {
        return find("oidPrestador=?1 and oidUsuario=?2", prestadorOid, usuarioOid)
                .firstResult();
    }
}
