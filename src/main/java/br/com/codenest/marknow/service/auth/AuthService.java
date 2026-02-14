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
import br.com.codenest.marknow.service.assinatura.AssinaturaPolitica;
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

    @Transactional
    public String validarAcesso(LoginRequest req) {
        // 1) busca usuário pelo email (case-insensitive)
        Usuario usuario = Usuario.find("lower(email) = ?1", req.email.toLowerCase()).firstResult();
        if (usuario == null) {
            throw BusinessException.of(ErrorCode.UNAUTHORIZED, "Credenciais inválidas.");
        }

        // 2) valida status do usuário
        if (usuario.status != UsuarioStatus.ATIVO) {
            throw BusinessException.of(ErrorCode.FORBIDDEN, "Usuário bloqueado.");
        }

        // 3) valida senha (placeholder)
        // TROQUE isso pelo seu hash real (BCrypt/Argon2).
        if (usuario.senha == null || !usuario.senha.equals(req.senha)) {
            throw BusinessException.of(ErrorCode.UNAUTHORIZED, "Credenciais inválidas.");
        }

        // 4) super admin global ignora assinatura/tenant (você decide)
        boolean superAdmin = Boolean.TRUE.equals(usuario.getAdmin());
        if (!superAdmin) {

            // 4.1) checa prestador
            Prestador prestador = Prestador.findById(req.prestadorOid);
            if (prestador == null) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Prestador inválido.");
            }
            if (prestador.status != PrestadorStatus.ATIVO) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Prestador inválido.");
            }

            // 4.2) checa vínculo usuário x prestador
            PrestadorUsuario pu = PrestadorUsuario
                    .find("oidPrestador=?1 and oidUsuario=?2", req.prestadorOid, usuario.getOid())
                    .firstResult();

            if (pu == null || pu.status != PrestadorUsuarioStatus.ATIVO) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Sem acesso a este prestador.");
            }

            // 4.3) checa assinatura do prestador
            Assinatura assinatura = Assinatura.find("oidPrestador", req.prestadorOid).firstResult();
            if (!politica.isAtiva(assinatura, Instant.now())) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Assinatura inválida.");
            }
        }

        // 5) gera token (com claim usuarioOid)
        return gerarToken(usuario);
    }

    @Transactional
    public void validarAcessoOuExplodir(Usuario u, Prestador p, PrestadorUsuario pu, Assinatura a) {
        if (u == null) throw new RuntimeException("Usuário não existe");
        if (u.status != UsuarioStatus.ATIVO) throw new RuntimeException("Usuário bloqueado");

        if (p == null) throw new RuntimeException("Prestador não existe");
        if (p.status != PrestadorStatus.ATIVO) throw new RuntimeException("Prestador bloqueado");

        if (pu == null || pu.status != PrestadorUsuarioStatus.ATIVO)
            throw new RuntimeException("Sem acesso ao prestador");

        // super admin global ignora assinatura? você decide.
        if (Boolean.TRUE.equals(u.admin)) return;

        AssinaturaPolitica policy = new AssinaturaPolitica();
        if (!policy.isAtiva(a, Instant.now())) throw new RuntimeException("Assinatura inválida");
    }

    @Transactional
    public String gerarToken(Usuario usuario) {
        return Jwt.issuer("marknow")
                .subject(usuario.email)
                .claim("usuarioOid", usuario.getOid())
                .expiresIn(Duration.ofHours(6))
                .sign();
    }

}
