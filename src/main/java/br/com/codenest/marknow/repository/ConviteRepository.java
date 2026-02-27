package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Convite;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

@ApplicationScoped
public class ConviteRepository implements PanacheRepositoryBase<Convite, String> {
    public Convite findByOid(String oid) {
        return find("oid", oid).firstResult();
    }

    public PanacheQuery<Convite> findWithPagination(int page, int size, String sort, String descricao) {
        String query = (descricao != null && !descricao.isBlank()) ? "descricao like ?1" : "1=1";
        String filter = (descricao != null && !descricao.isBlank()) ? "%" + descricao + "%" : "";
        return find(query, Sort.by(sort), filter).page(page, size);
    }

    public Convite findByCodigo(String codigo) {
        return find("codigo", codigo)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }
}
