package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Profissional;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProfissionalRepository implements PanacheRepositoryBase<Profissional, String> {

    public Profissional findByOidNoPrestador(String oid, String oidPrestador) {
        return find("oid = ?1 and oidPrestador = ?2", oid, oidPrestador).firstResult();
    }
}
