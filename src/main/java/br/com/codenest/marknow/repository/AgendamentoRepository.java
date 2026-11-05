package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Agendamento;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgendamentoRepository implements PanacheRepositoryBase<Agendamento, String> {

    public Agendamento findByOidNoPrestador(String oid, String oidPrestador) {
        return find("oid = ?1 and oidPrestador = ?2", oid, oidPrestador).firstResult();
    }
}
