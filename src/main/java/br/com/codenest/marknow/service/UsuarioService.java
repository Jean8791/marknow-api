package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.common.PageDTO;
import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import br.com.codenest.marknow.dto.usuario.UsuarioResponse;
import br.com.codenest.marknow.dto.usuario.UsuarioUpdateResquest;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.mapper.UsuarioMapper;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import br.com.codenest.marknow.repository.UsuarioRepository;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import br.com.codenest.marknow.util.UtilsSenha;
import br.com.codenest.marknow.util.UtilsString;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    UsuarioMapper mapper;

    @Inject
    PrestadorUsuarioService prestadorUsuarioService;

    @Inject
    PrestadorUsuarioRepository prestadorUsuarioRepository;

    @Inject
    AuditoriaService auditoriaService;

    @Transactional
    public UsuarioResponse createNoPrestadorLogado(@Valid UsuarioCreateResquest req) {
        req.setOidPrestador(auditoriaService.oidPrestadorLogadoOrThrow());
        if (req.getRole() == null) req.setRole(PrestadorUsuarioRole.OPERACIONAL);
        return create(req);
    }

    @Transactional
    public UsuarioResponse create(@Valid UsuarioCreateResquest req) {
        validacaoCreat(req);
        Usuario usuario = mapper.toEntity(req);
        usuarioRepository.persist(usuario);
        prestadorUsuarioService.create(usuario.getOid(), req);
        UsuarioResponse response = mapper.toResponse(usuario);
        response.setRole(req.getRole());
        return response;
    }

    @Transactional
    public UsuarioResponse update(String oid, @Valid UsuarioUpdateResquest req) {
        if (req == null || (UtilsString.temTexto(req.getOid()) && !req.getOid().equals(oid))) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "Usuário não encontrado.");
        }
        Usuario usuario = buscarNoPrestadorLogado(oid);
        PrestadorUsuario vinculo = vinculoNoPrestadorLogado(usuario.getOid());
        validacaoUpdate(usuario, vinculo, req);

        mapper.toUpdateEntity(usuario, req);
        if (UtilsString.temTexto(req.getSenha())) {
            usuario.setSenha(UtilsSenha.criptografar(req.getSenha()));
        }
        if (req.getRole() != null) {
            vinculo.setRole(req.getRole());
        }
        return toResponse(usuario, vinculo);
    }

    public UsuarioResponse get(String oid) {
        Usuario usuario = buscarNoPrestadorLogado(oid);
        return toResponse(usuario, vinculoNoPrestadorLogado(usuario.getOid()));
    }

    public PageDTO<UsuarioResponse> list(int page, int size, String sort, String search) {
        PanacheQuery<Usuario> query = usuarioRepository.findAll(page, size, sort, search);
        List<UsuarioResponse> content = query.list().stream()
                .map(u -> toResponse(u, vinculoNoPrestadorLogado(u.getOid())))
                .toList();
        return new PageDTO<>(content, query.count(), query.pageCount(), page, size);
    }

    @Transactional
    public void delete(String oid) {
        Usuario usuario = buscarNoPrestadorLogado(oid);
        if (isUsuarioLogado(usuario)) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Você não pode excluir o próprio usuário.");
        }
        prestadorUsuarioRepository.delete("oidUsuario", usuario.getOid());
        usuarioRepository.delete(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario, PrestadorUsuario vinculo) {
        UsuarioResponse response = mapper.toResponse(usuario);
        if (vinculo != null) response.setRole(vinculo.getRole());
        return response;
    }

    private Usuario buscarNoPrestadorLogado(String oid) {
        Usuario usuario = usuarioRepository.findByOidNoPrestador(oid, auditoriaService.oidPrestadorLogadoOrThrow());
        if (usuario == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Usuário não encontrado.");
        return usuario;
    }

    private PrestadorUsuario vinculoNoPrestadorLogado(String oidUsuario) {
        return prestadorUsuarioRepository.findByOidPrestadorAndOidUsuario(auditoriaService.oidPrestadorLogadoOrThrow(), oidUsuario);
    }

    private boolean isUsuarioLogado(Usuario usuario) {
        return usuario.getOid().equals(auditoriaService.oidUsuarioLogadoOrNull());
    }

    private void validacaoCreat(UsuarioCreateResquest req) {
        if (req == null) {
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "Erro inesperado.");
        }

        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw BusinessException.of(ErrorCode.CONFLICT, "Já existe usuário cadastrado com este e-mail.");
        }

        if (!UtilsString.temTexto(req.getSenha())) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "A senha é obrigatória para novos usuários.");
        }
        if (!req.getSenha().equals(req.getComfirmarSenha())) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "A confirmação de senha não confere.");
        }
        req.setSenha(UtilsSenha.criptografar(req.getSenha()));
    }

    private void validacaoUpdate(Usuario usuario, PrestadorUsuario vinculo, UsuarioUpdateResquest req) {
        if (usuarioRepository.existsByEmailEmOutroRegistro(usuario.getOid(), req.getEmail())) {
            throw BusinessException.of(ErrorCode.CONFLICT, "Já existe usuário cadastrado com este e-mail.");
        }

        if (UtilsString.temTexto(req.getSenha()) && !req.getSenha().equals(req.getComfirmarSenha())) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "A confirmação de senha não confere.");
        }

        if (isUsuarioLogado(usuario)) {
            if (req.getStatus() != null && req.getStatus() != usuario.getStatus()) {
                throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Você não pode alterar o status do próprio usuário.");
            }
            if (req.getRole() != null && vinculo != null && req.getRole() != vinculo.getRole()) {
                throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Você não pode alterar o próprio perfil de acesso.");
            }
        }
    }
}
