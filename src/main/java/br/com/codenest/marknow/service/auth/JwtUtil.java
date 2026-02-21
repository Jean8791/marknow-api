package br.com.codenest.marknow.service.auth;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class JwtUtil {

    @Inject
    JsonWebToken jwt;

    /**
     * Espera um claim "usuarioOid" no token.
     */
    public String getUsuarioOidOrNull() {
        Object v = jwt.getClaim("usuarioOid");
        return v == null ? null : String.valueOf(v);
    }

    public String getPrestadorOidOrNull() {
        Object v = jwt.getClaim("prestadorOid");
        return v == null ? null : String.valueOf(v);
    }

    public String getUsuarioOidOrThrow() {
        String oid = getUsuarioOidOrNull();
        if (oid == null || oid.isBlank()) {
            throw new IllegalStateException("Token sem claim usuarioOid");
        }
        return oid;
    }

    public String getPrestadorOidOrThrow() {
        String oid = getPrestadorOidOrNull();
        if (oid == null || oid.isBlank()) {
            throw new IllegalStateException("Token sem claim prestadorOid");
        }
        return oid;
    }

}
