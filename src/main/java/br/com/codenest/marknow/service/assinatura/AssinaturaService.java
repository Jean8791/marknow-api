package br.com.codenest.marknow.service.assinatura;

import br.com.codenest.marknow.dto.assinatura.AssinaturaResponse;
import br.com.codenest.marknow.dto.common.PageDTO;
import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.mapper.AssinaturaMapper;
import br.com.codenest.marknow.repository.AssinaturaRepository;
import br.com.codenest.marknow.util.PaginationUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class AssinaturaService {

    @Inject
    AssinaturaRepository assinaturaRepository;

    @Inject
    AssinaturaMapper mapper;

    public PageDTO<AssinaturaResponse> list(int page, int size, String sort, String search) {
        String termo = search == null || search.isBlank() ? "" : search.trim().toLowerCase();
        PanacheQuery<Assinatura> query = assinaturaRepository.find(
                "?1 = '' or lower(plano.nome) like concat('%', ?1, '%') or lower(prestador.nome) like concat('%', ?1, '%') or lower(coalesce(referenciaProvedor, '')) like concat('%', ?1, '%')",
                Sort.by(PaginationUtil.campoOrdenacao(sort, Set.of("criadoEm", "status", "fimPeriodoAtual"), "criadoEm")).descending(),
                termo).page(page, size);
        List<AssinaturaResponse> content = query.list().stream().map(mapper::toResponse).toList();
        return new PageDTO<>(content, query.count(), query.pageCount(), page, size);
    }
}
