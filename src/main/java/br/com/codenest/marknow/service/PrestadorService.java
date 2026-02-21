package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.prestador.PrestadorCreateRequest;
import br.com.codenest.marknow.dto.prestador.PrestadorResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorUpdateRequest;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.mapper.PrestadorMapper;
import br.com.codenest.marknow.repository.PrestadorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class PrestadorService {

    @Inject
    PrestadorRepository repo;

    @Inject
    PrestadorMapper mapper;

    @Transactional
    public PrestadorResponse create(@Valid PrestadorCreateRequest req) {
        validacaoCreat(req);
        Prestador prestador = mapper.toEntity(req);
        repo.persist(prestador);

        return mapper.toResponse(prestador);
    }

    @Transactional
    public PrestadorResponse update(String oid, @Valid PrestadorUpdateRequest req) {
        Prestador prestador = repo.findById(req.getOid());
        validacaoUpdate(prestador, req);
        mapper.toUpdateEntity(prestador, req);

        return mapper.toResponse(prestador);
    }

    public PrestadorResponse get(String oid) {
        Prestador prestador = repo.findById(oid);
        if (prestador == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Prestador não encontrado.");
        return mapper.toResponse(prestador);
    }

    public List<PrestadorResponse> list(int page, int size, String sort) {
        return repo.findAll(page, size, sort).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public void delete(String oid) {
        boolean deleted = repo.deleteById(oid);
        if (!deleted) throw BusinessException.of(ErrorCode.NOT_FOUND, "Prestador não encontrado.");
    }

    private void validacaoCreat(PrestadorCreateRequest req) {
        if (req == null) {
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "Erro inesperado.");
        }

        if (repo.existsByDocumento(req.getDocumento())) {
            throw BusinessException.of(ErrorCode.CONFLICT, "Já existe prestador cadastrada com este documento.");
        }
    }

    private void validacaoUpdate(Prestador prestador, PrestadorUpdateRequest req) {
        if (req == null) {
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "Erro inesperado.");
        }

        if (repo.existsByDocumentoEmOutroRegistro(prestador.getOid(), req.getDocumento())) {
            throw BusinessException.of(ErrorCode.CONFLICT, "Já existe prestador cadastrada com este documento.");
        }
    }
}
