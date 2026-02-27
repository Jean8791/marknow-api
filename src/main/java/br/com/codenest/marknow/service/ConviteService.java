package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.common.PageDTO;
import br.com.codenest.marknow.dto.convite.ConviteCreateRequest;
import br.com.codenest.marknow.dto.convite.ConviteResponse;
import br.com.codenest.marknow.entity.Convite;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.mapper.ConviteMapper;
import br.com.codenest.marknow.repository.ConviteRepository;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ConviteService {

    @Inject
    ConviteRepository conviteRepository;

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    ConviteMapper mapper;

    @Transactional
    public ConviteResponse create(@Valid ConviteCreateRequest req) {
        validar(req);
        Convite convite = mapper.toEntity(req);
        convite.setOidUsuarioCriacao(auditoriaService.oidUsuarioLogadoOrThrow());
        conviteRepository.persist(convite);
        return mapper.toResponse(convite);
    }

    public ConviteResponse get(String oid) {
        Convite convite = conviteRepository.findByOid(oid);
        if (convite == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Convite não encontrado.");
        return mapper.toResponse(convite);
    }

    public PageDTO<ConviteResponse> list(int page, int size, String descricao) {
        Map<String, Object> params = new HashMap<>();
        String queryStr;

        if (descricao != null && !descricao.isBlank()) {
            queryStr = "descricao like :descricao";
            params.put("descricao", "%" + descricao + "%");
        } else {
            queryStr = "1=1";
        }

        PanacheQuery<Convite> query = conviteRepository.find(queryStr, Sort.by("expiraEm").descending(), params).page(page, size);

        List<ConviteResponse> content = query.list().stream().map(mapper::toResponse).toList();

        return new PageDTO<>(content, query.count(), query.pageCount(), page, size);
    }

    @Transactional
    public void delete(String oid) {
        boolean deletado = conviteRepository.deleteById(oid);
        if (!deletado) throw BusinessException.of(ErrorCode.NOT_FOUND, "Convite não encontrado.");
    }

    private void validar(ConviteCreateRequest req) {
        if (req == null) {
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "Erro inesperado.");
        }
    }
}
