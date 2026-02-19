package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.cadastro.CadastroCreateRequest;
import br.com.codenest.marknow.dto.cadastro.CadastroResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorResponse;
import br.com.codenest.marknow.dto.usuario.UsuarioResponse;
import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.mapper.UsuarioMapper;
import br.com.codenest.marknow.repository.AssinaturaRepository;
import br.com.codenest.marknow.repository.PrestadorRepository;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import br.com.codenest.marknow.repository.UsuarioRepository;
import br.com.codenest.marknow.service.auth.AuthService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class CadastroService {

    @Inject
    AuthService authService;
    @Inject
    UsuarioRepository usuarioRepository;
    @Inject
    PrestadorRepository prestadorRepository;
    @Inject
    PrestadorUsuarioRepository prestadorUsuarioRepository;
    @Inject
    AssinaturaRepository assinaturaRepository;
    @Inject
    UsuarioMapper usuarioMapper;
    @Inject
    PrestadorService prestadorService;
    @Inject
    UsuarioService usuarioService;

    @Transactional
    public CadastroResponse create(@Valid CadastroCreateRequest req) {

        if (req == null || req.getUsuario() == null || req.getPrestador() == null) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Payload inválido (usuario e prestador são obrigatórios).");
        }

        String email = req.getUsuario().getEmail();
        if (email == null || email.isBlank()) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "email é obrigatório.");
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw BusinessException.of(ErrorCode.CONFLICT, "Já existe usuário cadastrado com este e-mail.");
        }

        PrestadorResponse prestador = prestadorService.create(req.getPrestador());
        UsuarioResponse usuario = usuarioService.create(req.getUsuario(), prestador.getOid());

        PrestadorUsuario pu = new PrestadorUsuario();
        pu.setOidPrestador(prestador.getOid());
        pu.setOidUsuario(usuario.getOid());
        pu.setRole(PrestadorUsuarioRole.ADMIN);
        prestadorUsuarioRepository.persist(pu);

        Instant agora = Instant.now();
        int diasTrial = req.getDiasTrial() != null ? req.getDiasTrial() : 7;

        Assinatura assinatura = new Assinatura();
        assinatura.setOidPrestador(prestador.getOid());
        assinatura.setCriadoPor(usuario.getOid());
        assinatura.setPlano(req.getPlano());
        assinatura.setStatus(AssinaturaStatus.TRIAL);
        assinatura.setFimPeriodoAtual(agora.plus(diasTrial, ChronoUnit.DAYS));
        assinatura.setProvedor("INTERNO");
        assinaturaRepository.persist(assinatura);

        String token = authService.gerarToken(usuario.getOid(), email);
        return new CadastroResponse(prestador.getOid(), token);
    }
}
