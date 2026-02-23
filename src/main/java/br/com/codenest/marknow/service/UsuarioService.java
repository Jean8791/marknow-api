package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import br.com.codenest.marknow.dto.usuario.UsuarioResponse;
import br.com.codenest.marknow.dto.usuario.UsuarioUpdateResquest;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.mapper.UsuarioMapper;
import br.com.codenest.marknow.repository.UsuarioRepository;
import br.com.codenest.marknow.util.UtilsSenha;
import br.com.codenest.marknow.util.UtilsString;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository repo;

    @Inject
    UsuarioMapper mapper;

    @Inject
    PrestadorUsuarioService prestadorUsuarioService;

    @Transactional
    public UsuarioResponse create(@Valid UsuarioCreateResquest req) {
        validacaoCreat(req);
        Usuario usuario = mapper.toEntity(req);
        repo.persist(usuario);
        prestadorUsuarioService.create(usuario.getOid(), req.getOidPrestador());
        return mapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse update(@Valid UsuarioUpdateResquest req) {
        Usuario usuario = repo.findById(req.getOid());
        validacaoUpdate(usuario, req);
        mapper.toUpdateEntity(usuario, req);
        return mapper.toResponse(usuario);
    }

    public UsuarioResponse get(String oid) {
        Usuario usuario = repo.findById(oid);
        if (usuario == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Usuário não encontrado.");
        return mapper.toResponse(usuario);
    }

    public List<UsuarioResponse> list(int page, int size, String sort) {
        return repo.findAll(page, size, sort).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public void delete(String oid) {
        boolean deletado = repo.deleteById(oid);
        if (!deletado) throw BusinessException.of(ErrorCode.NOT_FOUND, "Usuário não encontrado.");
    }


    private void validacaoCreat(UsuarioCreateResquest req) {
        if (req == null) {
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "Erro inesperado.");
        }

        if (repo.existsByEmail(req.getEmail())) {
            throw BusinessException.of(ErrorCode.CONFLICT, "Já existe usuário cadastrado com este e-mail.");
        }

        if (UtilsString.temTexto(req.getSenha())) {
            req.setSenha(UtilsSenha.criptografar(req.getSenha()));
        } else {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "A senha é obrigatória para novos usuários.");
        }
    }

    private void validacaoUpdate(Usuario usuario, UsuarioUpdateResquest req) {
        if (req == null) {
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "Erro inesperado.");
        }

        if (repo.existsByEmailEmOutroRegistro(usuario.getOid(), req.getEmail())) {
            throw BusinessException.of(ErrorCode.CONFLICT, "Já existe usuário cadastrado com este e-mail.");
        }
    }
}
