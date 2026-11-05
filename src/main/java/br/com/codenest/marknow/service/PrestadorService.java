package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.common.PageDTO;
import br.com.codenest.marknow.dto.prestador.PrestadorCreateRequest;
import br.com.codenest.marknow.dto.prestador.PrestadorDetalhadoResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorUpdateRequest;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.mapper.PrestadorMapper;
import br.com.codenest.marknow.repository.AssinaturaRepository;
import br.com.codenest.marknow.repository.PrestadorRepository;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import br.com.codenest.marknow.repository.UsuarioRepository;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import br.com.codenest.marknow.service.demo.DemoConfig;
import br.com.codenest.marknow.util.PaginationUtil;
import br.com.codenest.marknow.util.UtilsString;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class PrestadorService {

    @Inject
    PrestadorRepository repo;

    @Inject
    PrestadorMapper mapper;

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    DemoConfig demoConfig;

    @Inject
    AssinaturaRepository assinaturaRepository;

    @Inject
    PrestadorUsuarioRepository prestadorUsuarioRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    public PrestadorResponse create(@Valid PrestadorCreateRequest req) {
        validacaoCreat(req);
        Prestador prestador = mapper.toEntity(req);
        prestador.setStatus(PrestadorStatus.ATIVO);
        repo.persist(prestador);
        return mapper.toResponse(prestador);
    }

    @Transactional
    public PrestadorResponse update(String oid, @Valid PrestadorUpdateRequest req) {
        if (req == null || !UtilsString.temTexto(oid) || !oid.equals(req.getOid())) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "Prestador não encontrado.");
        }
        Prestador prestador = buscarPrestadorLogado(oid);
        demoConfig.bloquearSeDemo(auditoriaService.emailUsuarioLogadoOrNull(), "Os dados do prestador de demonstração não podem ser alterados.");
        validacaoUpdate(prestador, req);
        mapper.toUpdateEntity(prestador, req);

        return mapper.toResponse(prestador);
    }

    @Transactional
    public PrestadorResponse get(String oid) {
        return mapper.toResponse(buscarPrestadorLogado(oid));
    }

    private Prestador buscarPrestadorLogado(String oid) {
        if (oid == null || !oid.equals(auditoriaService.oidPrestadorLogadoOrThrow())) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "Prestador não encontrado.");
        }
        Prestador prestador = repo.findById(oid);
        if (prestador == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Prestador não encontrado.");
        return prestador;
    }

    public PageDTO<PrestadorDetalhadoResponse> listDetailed(int page, int size, String sort, String search) {
        String field = "p." + PaginationUtil.campoOrdenacao(sort, Set.of("nome", "documento", "status", "criadoEm"), "criadoEm");

        PanacheQuery<PrestadorDetalhadoResponse> query = repo.findDetailed(Sort.by(field), search);
        query.page(page, size);

        return PaginationUtil.toPageDTO(query, dto -> dto);
    }

    @Transactional
    public void delete(String oid) {
        Prestador prestador = repo.findById(oid);
        if (prestador == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Prestador não encontrado.");

        List<PrestadorUsuario> vinculos = prestadorUsuarioRepository.list("oidPrestador", oid);
        assinaturaRepository.list("oidPrestador", oid).forEach(assinaturaRepository::delete);
        vinculos.forEach(prestadorUsuarioRepository::delete);
        prestadorUsuarioRepository.flush();

        for (PrestadorUsuario vinculo : vinculos) {
            if (prestadorUsuarioRepository.count("oidUsuario", vinculo.getOidUsuario()) == 0) {
                usuarioRepository.deleteById(vinculo.getOidUsuario());
            }
        }
        repo.delete(prestador);
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
