package br.com.codenest.marknow.service.auth;

import br.com.codenest.marknow.dto.auth.LoginRequest;
import br.com.codenest.marknow.dto.auth.LoginResponse;
import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.enums.PrestadorUsuarioStatus;
import br.com.codenest.marknow.enums.UsuarioStatus;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.mapper.LoginMapper;
import br.com.codenest.marknow.repository.AssinaturaRepository;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import br.com.codenest.marknow.repository.UsuarioRepository;
import br.com.codenest.marknow.service.assinatura.AssinaturaPolitica;
import br.com.codenest.marknow.util.UtilsSenha;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;

@ApplicationScoped
public class AuthService {

    @Inject
    JwtUtil jwtUtil;

    @Inject
    AssinaturaPolitica politica;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    AssinaturaRepository assinaturaRepository;

    @Inject
    PrestadorUsuarioRepository prestadorUsuarioRepository;

    @Inject
    LoginMapper mapper;

    @Transactional
    public LoginResponse validarAcesso(LoginRequest req) {
        Usuario usuario = usuarioRepository.find("email", req.email.toLowerCase()).firstResult();

        if (usuario == null) {
            throw BusinessException.of(ErrorCode.UNAUTHORIZED, "Credenciais inválidas.");
        }

        if (!UsuarioStatus.ATIVO.equals(usuario.getStatus())) {
            throw BusinessException.of(ErrorCode.FORBIDDEN, "Usuário bloqueado.");
        }

        if (!UtilsSenha.verificar(req.senha, usuario.getSenha())) {
            throw BusinessException.of(ErrorCode.UNAUTHORIZED, "Credenciais inválidas.");
        }

        String oidPrestadorParaToken = null;
        PrestadorUsuarioRole role = null;
        boolean superAdmin = Boolean.TRUE.equals(usuario.getAdmin());

        if (!superAdmin) {

            PrestadorUsuario pu = prestadorUsuarioRepository.findByOidUsuario(usuario.getOid());

            if (pu == null) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Usuário não vinculado a nenhum prestador ativo.");
            }

            Prestador prestador = Prestador.findById(pu.getOidPrestador());
            if (prestador == null || prestador.getStatus() != PrestadorStatus.ATIVO) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Prestador inválido ou bloqueado.");
            }

            Assinatura assinatura = Assinatura.find("oidPrestador", prestador.getOid()).firstResult();
            if (!politica.isAtiva(assinatura, Instant.now())) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Acesso bloqueado: Verifique a assinatura do plano.");
            }

            oidPrestadorParaToken = prestador.getOid();
            role = pu.getRole();
        }

        return mapper.toResponse(usuario, jwtUtil.gerarToken(oidPrestadorParaToken, usuario.getOid(), usuario.getEmail(), role));
    }

}
