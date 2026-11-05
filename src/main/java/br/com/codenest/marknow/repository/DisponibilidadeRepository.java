package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Disponibilidade;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DisponibilidadeRepository implements PanacheRepositoryBase<Disponibilidade, String> {

    public Disponibilidade findByOidNoPrestador(String oid, String oidPrestador) {
        return find("oid = ?1 and oidPrestador = ?2", oid, oidPrestador).firstResult();
    }
}
