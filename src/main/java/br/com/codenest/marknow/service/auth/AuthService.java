package br.com.codenest.marknow.service.auth;

import br.com.codenest.marknow.dto.auth.LoginRequest;
import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.enums.PrestadorUsuarioStatus;
import br.com.codenest.marknow.enums.UsuarioStatus;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import br.com.codenest.marknow.repository.UsuarioRepository;
import br.com.codenest.marknow.service.assinatura.AssinaturaPolitica;
import br.com.codenest.marknow.util.UtilsSenha;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class AuthService {

    @Inject
    AssinaturaPolitica politica;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    PrestadorUsuarioRepository prestadorUsuarioRepository;

    @Transactional
    public String validarAcesso(LoginRequest req) {
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
        boolean superAdmin = Boolean.TRUE.equals(usuario.getAdmin());

        if (!superAdmin) {
            PrestadorUsuario pu = prestadorUsuarioRepository.find("oidUsuario=?1 and status=?2",
                    usuario.getOid(), PrestadorUsuarioStatus.ATIVO).firstResult();

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
        }

        return gerarToken(oidPrestadorParaToken, usuario.getOid(), usuario.getEmail());
    }

    public String gerarToken(String prestadorOid, String usuarioOid, String email) {
        var claims = Jwt.issuer("marknow")
                .subject(email)
                .claim("usuarioOid", usuarioOid);

        if (prestadorOid != null) {
            claims.claim("prestadorOid", prestadorOid);
        } else {
            claims.claim("superAdmin", true);
        }

        return claims.expiresIn(Duration.ofHours(6)).sign();
    }

}
