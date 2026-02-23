package br.com.codenest.marknow.security;

import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.enums.PrestadorUsuarioStatus;
import br.com.codenest.marknow.enums.UsuarioStatus;
import br.com.codenest.marknow.repository.AssinaturaRepository;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import br.com.codenest.marknow.service.assinatura.AssinaturaPolitica;
import br.com.codenest.marknow.service.auth.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
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
    JwtUtil jwtUtil;

    @Inject
    EntityManager em;

    @Inject
    TenantContext tenantContext;

    @Inject
    AssinaturaPolitica politica;

    @Inject
    AssinaturaRepository assinaturaRepository;

    @Inject
    PrestadorUsuarioRepository prestadorUsuarioRepository;

    @Override
    public void filter(ContainerRequestContext ctx) {

        String path = ctx.getUriInfo().getPath();

        if (path.startsWith("/auth") || path.startsWith("/cadastro")) return;

        String usuarioOid = jwtUtil.getUsuarioOidOrNull();
        if (usuarioOid == null) {
            abort(ctx, "Token inválido: Usuário não identificado");
            return;
        }

        Usuario u = Usuario.findById(usuarioOid);
        if (u == null || !UsuarioStatus.ATIVO.equals(u.getStatus())) {
            abort(ctx, "Acesso negado: Usuário inexistente ou bloqueado");
            return;
        }

        tenantContext.usuario = u;
        tenantContext.superAdmin = Boolean.TRUE.equals(u.getAdmin());

        if (tenantContext.superAdmin) {
            return;
        }

        String prestadorOid = jwtUtil.getPrestadorOidOrNull();
        if (prestadorOid == null) {
            abort(ctx, "Token inválido: Prestador não identificado");
            return;
        }

        Prestador p = Prestador.findById(prestadorOid);
        if (p == null || !PrestadorStatus.ATIVO.equals(p.getStatus())) {
            abort(ctx, "Acesso negado: Prestador bloqueado ou inválido");
            return;
        }

        tenantContext.prestadorOid = prestadorOid;

        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.enableFilter("tenantFilter")
                .setParameter("oidPrestador", tenantContext.prestadorOid);

        PrestadorUsuario pu = prestadorUsuarioRepository.findByOidPrestadorAndOidUsuario(prestadorOid, usuarioOid);

        if (pu == null || pu.getStatus() != PrestadorUsuarioStatus.ATIVO) {
            abort(ctx, "Acesso negado: Você não tem permissão para acessar este prestador");
            return;
        }

        String roleToken = jwtUtil.getRoleOrNull();
        if (roleToken != null) {
            tenantContext.rolePrestador = PrestadorUsuarioRole.valueOf(roleToken);
        }

        Assinatura a = assinaturaRepository.findByOidPrestador(prestadorOid);
        if (!politica.isAtiva(a, Instant.now())) {
            abort(ctx, "Acesso negado: Assinatura do plano expirada ou inválida");
            return;
        }
    }

    private void abort(ContainerRequestContext ctx, String msg) {
        ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity(new Error(msg))
                .build());
    }

    public record Error(String message) {
    }
}
