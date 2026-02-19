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
import br.com.codenest.marknow.repository.UsuarioRepository;
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

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    public String validarAcesso(LoginRequest req) {

        Usuario usuario = usuarioRepository.findByEmail(req.email.toLowerCase());
        if (usuario == null) {
            throw BusinessException.of(ErrorCode.UNAUTHORIZED, "Credenciais inválidas.");
        }

        if (!UsuarioStatus.ATIVO.equals(usuario.getStatus())) {
            throw BusinessException.of(ErrorCode.FORBIDDEN, "Usuário bloqueado.");
        }

        if (!usuario.getSenha().equals(req.senha)) {
            throw BusinessException.of(ErrorCode.UNAUTHORIZED, "Credenciais inválidas.");
        }

        boolean superAdmin = Boolean.TRUE.equals(usuario.getAdmin());

        if (!superAdmin) {

            Prestador prestador = Prestador.findById(req.prestadorOid);
            if (prestador == null) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Prestador inválido.");
            }
            if (prestador.getStatus() != PrestadorStatus.ATIVO) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Prestador inválido.");
            }

            PrestadorUsuario pu = PrestadorUsuario
                    .find("oidPrestador=?1 and oidUsuario=?2", req.prestadorOid, usuario.getOid())
                    .firstResult();

            if (pu == null || pu.getStatus() != PrestadorUsuarioStatus.ATIVO) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Sem acesso a este prestador.");
            }

            Assinatura assinatura = Assinatura.find("oidPrestador", req.prestadorOid).firstResult();
            if (!politica.isAtiva(assinatura, Instant.now())) {
                throw BusinessException.of(ErrorCode.FORBIDDEN, "Assinatura inválida.");
            }
        }

        return gerarToken(usuario.getOid(), usuario.getEmail());
    }

    @Transactional
    public String gerarToken(String oid, String email) {
        return Jwt.issuer("marknow")
                .subject(email)
                .claim("usuarioOid", oid)
                .expiresIn(Duration.ofHours(6))
                .sign();
    }

}
