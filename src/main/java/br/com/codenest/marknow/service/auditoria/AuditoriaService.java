package br.com.codenest.marknow.service.auditoria;

import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuditoriaService {

    @Inject
    TenantContext ctx;

    public String oidUsuarioLogadoOrThrow() {
        if (ctx.usuario == null || ctx.usuario.getOid() == null) {
            throw BusinessException.of(ErrorCode.UNAUTHORIZED, "Sessão de usuário não encontrada para auditoria.");
        }
        return ctx.usuario.getOid();
    }

    public String oidPrestadorLogadoOrThrow() {
        if (ctx.prestadorOid == null || ctx.prestadorOid.isBlank()) {
            throw BusinessException.of(ErrorCode.UNAUTHORIZED, "Prestador não identificado no contexto da requisição.");
        }
        return ctx.prestadorOid;
    }

    public String oidUsuarioLogadoOrNull() {
        return (ctx.usuario != null) ? ctx.usuario.getOid() : null;
    }
}