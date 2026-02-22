package br.com.codenest.marknow.service.auth;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

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

    public boolean isSuperAdmin() {
        Object v = jwt.getClaim("superAdmin");
        return v != null && Boolean.parseBoolean(String.valueOf(v));
    }

    public String getUsuarioOidOrThrow() {
        String oid = getUsuarioOidOrNull();
        if (oid == null || oid.isBlank()) throw new IllegalStateException("Token sem usuarioOid");
        return oid;
    }

    public String getPrestadorOidOrThrow() {
        String oid = getPrestadorOidOrNull();
        if (oid == null || oid.isBlank()) throw new IllegalStateException("Token sem prestadorOid");
        return oid;
    }
}
