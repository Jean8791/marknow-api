package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.cadastro.CadastroCreateRequest;
import br.com.codenest.marknow.dto.cadastro.CadastroResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorResponse;
import br.com.codenest.marknow.dto.usuario.UsuarioResponse;
import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.Plano;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.enums.AssinaturaStatus;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.repository.AssinaturaRepository;
import br.com.codenest.marknow.repository.PlanoRepository;
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
    PrestadorUsuarioRepository prestadorUsuarioRepository;
    @Inject
    AssinaturaRepository assinaturaRepository;
    @Inject
    PrestadorService prestadorService;
    @Inject
    UsuarioService usuarioService;

    @Inject
    PlanoRepository planoRepository;

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

        Plano plano = planoRepository.findByOid(req.getOidPlano());

        if (plano == null) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Plano inválido");
        }

        PrestadorUsuario pu = new PrestadorUsuario();
        pu.setOidPrestador(prestador.getOid());
        pu.setOidUsuario(usuario.getOid());
        pu.setRole(PrestadorUsuarioRole.ADMIN);
        prestadorUsuarioRepository.persist(pu);

        Instant agora = Instant.now();

        Assinatura assinatura = new Assinatura();
        assinatura.setOidPrestador(prestador.getOid());
        assinatura.setCriadoPor(usuario.getOid());
        assinatura.setPlano(plano);

        if (plano.getPossuiTrial()) {
            assinatura.setStatus(AssinaturaStatus.TRIAL);
            assinatura.setFimPeriodoAtual(agora.plus(plano.getDiasTrial(), ChronoUnit.DAYS));
        } else {
            assinatura.setStatus(AssinaturaStatus.ATIVA);
            assinatura.setFimPeriodoAtual(agora.plus(30, ChronoUnit.DAYS));
        }

        assinatura.setProvedor("INTERNO");
        assinaturaRepository.persist(assinatura);

        String token = authService.gerarToken(prestador.getOid(), usuario.getOid(), email);

        return new CadastroResponse(prestador.getOid(), token);
    }
}
