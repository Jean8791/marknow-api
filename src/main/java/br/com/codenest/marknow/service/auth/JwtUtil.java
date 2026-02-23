package br.com.codenest.marknow.service.auth;


import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;

@ApplicationScoped
public class JwtUtil {
    @Inject
    JsonWebToken jwt;

    public String getUsuarioOidOrNull() {
        Object v = jwt.getClaim("usuarioOid");
        return v == null ? null : String.valueOf(v);
    }

    public String getPrestadorOidOrNull() {
        Object v = jwt.getClaim("prestadorOid");
        return v == null ? null : String.valueOf(v);
    }

    public String getRoleOrNull() {
        Object v = jwt.getClaim("role");
        return v == null ? null : String.valueOf(v);
    }

    public boolean isSuperAdmin() {
        Object v = jwt.getClaim("superAdmin");
        return v != null && Boolean.parseBoolean(String.valueOf(v));
    }

    public String gerarToken(String prestadorOid, String usuarioOid, String email, PrestadorUsuarioRole role) {
        var claims = Jwt.issuer("marknow")
                .subject(email)
                .claim("usuarioOid", usuarioOid);

        if (prestadorOid != null) {
            claims.claim("prestadorOid", prestadorOid);
            claims.claim("role", role.name());
        } else {
            claims.claim("superAdmin", true);
            claims.claim("role", "SUPER_ADMIN");
        }

        return claims.expiresIn(Duration.ofHours(6)).sign();
    }
}
