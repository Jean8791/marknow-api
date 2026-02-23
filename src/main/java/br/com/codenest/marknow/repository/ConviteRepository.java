package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Convite;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

import java.util.List;

@ApplicationScoped
public class ConviteRepository implements PanacheRepositoryBase<Convite, String> {
    public Convite findByOid(String oid) {
        return find("oid", oid).firstResult();
    }

    public List<Convite> findAll(int page, int size, String sort) {
        Sort panacheSort = Sort.by(sort);
        return findAll(panacheSort).page(page, size).list();
    }

    public Convite findByCodigo(String codigo) {
        return find("codigo", codigo)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }
}
