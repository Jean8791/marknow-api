package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.enums.PrestadorUsuarioStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrestadorUsuarioRepository implements PanacheRepositoryBase<PrestadorUsuario, String> {
    public PrestadorUsuario findByOidPrestadorAndOidUsuario(String prestadorOid, String usuarioOid) {
        return find("oidPrestador=?1 and oidUsuario=?2", prestadorOid, usuarioOid)
                .firstResult();
    }

    public PrestadorUsuario findByOidUsuario(String oid) {
        return find("oidUsuario = ?1 and status = ?2", oid, PrestadorUsuarioStatus.ATIVO).firstResult();
    }
}
