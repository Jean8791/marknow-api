package br.com.codenest.marknow.security;

import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.enums.PrestadorUsuarioStatus;
import br.com.codenest.marknow.enums.UsuarioStatus;
import br.com.codenest.marknow.service.assinatura.AssinaturaPolitica;
import br.com.codenest.marknow.service.auth.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;

@Provider
@Transactional
@Priority(Priorities.AUTHORIZATION)
public class TenantAssinaturaFilter implements ContainerRequestFilter {

    @Inject
    TenantContext tenantContext;
    @Inject
    JwtUtil jwtUtil;
    @Inject
    AssinaturaPolitica politica;

    @Override
    public void filter(ContainerRequestContext ctx) {

        String path = ctx.getUriInfo().getPath();
        if (path.startsWith("auth")) return;

        String prestadorOid = ctx.getHeaderString("X-Prestador-Oid");
        if (prestadorOid == null || prestadorOid.isBlank()) {
            abort(ctx, "Faltou header X-Prestador-Oid");
            return;
        }

        String usuarioOid = jwtUtil.getUsuarioOidOrNull();
        if (usuarioOid == null) {
            abort(ctx, "Token inválido");
            return;
        }

        Usuario u = Usuario.findById(usuarioOid);
        if (u == null || u.status != UsuarioStatus.ATIVO) {
            abort(ctx, "Usuário bloqueado");
            return;
        }

        tenantContext.usuario = u;
        tenantContext.prestadorOid = prestadorOid;
        tenantContext.superAdmin = u.getAdmin();

        if (tenantContext.superAdmin) return;

        Prestador p = Prestador.findById(prestadorOid);
        if (p == null || p.status != PrestadorStatus.ATIVO) {
            abort(ctx, "Prestador bloqueado");
            return;
        }

        PrestadorUsuario pu = PrestadorUsuario
                .find("oidPrestador=?1 and oidUsuario=?2", prestadorOid, usuarioOid)
                .firstResult();

        if (pu == null || pu.status != PrestadorUsuarioStatus.ATIVO) {
            abort(ctx, "Sem acesso ao prestador");
            return;
        }

        Assinatura a = Assinatura.find("oidPrestador", prestadorOid).firstResult();
        if (!politica.isAtiva(a, Instant.now())) {
            abort(ctx, "Assinatura inválida");
            return;
        }
    }

    private void abort(ContainerRequestContext ctx, String msg) {
        ctx.abortWith(Response.status(Response.Status.FORBIDDEN).entity(new Error(msg)).build());
    }

    public record Error(String message) {
    }
}
