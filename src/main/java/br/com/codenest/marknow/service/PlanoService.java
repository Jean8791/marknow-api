package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.plano.PlanoCreateRequest;
import br.com.codenest.marknow.dto.plano.PlanoResponse;
import br.com.codenest.marknow.dto.plano.PlanoUpdateRequest;
import br.com.codenest.marknow.entity.Plano;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.mapper.PlanoMapper;
import br.com.codenest.marknow.repository.PlanoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class PlanoService {

    @Inject
    PlanoMapper mapper;

    @Inject
    PlanoRepository planoRepository;

    @Transactional
    public PlanoResponse create(@Valid PlanoCreateRequest req) {
        Plano plano = mapper.toEntity(req);
        planoRepository.persist(plano);
        return mapper.toResponse(plano);
    }

    @Transactional
    public PlanoResponse update(@Valid PlanoUpdateRequest req) {
        Plano plano = planoRepository.findByOid(req.getOid());
        validarUpdate(plano, req);
        mapper.toUpdateEntity(plano, req);
        return mapper.toResponse(plano);
    }

    public PlanoResponse get(String oid) {
        Plano plano = planoRepository.findByOid(oid);
        if (plano == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Plano não encontrado.");
        return mapper.toResponse(plano);
    }

    public List<PlanoResponse> list(int page, int size, String sort) {
        return planoRepository.findAll(page, size, sort).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public void delete(String oid) {
        boolean deletado = planoRepository.deleteById(oid);
        if (!deletado) throw BusinessException.of(ErrorCode.NOT_FOUND, "Plano não encontrado.");
    }

    private void validarUpdate(Plano plano, PlanoUpdateRequest req) {
        if (req == null) {
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "Erro inesperado.");
        }
    }
}
