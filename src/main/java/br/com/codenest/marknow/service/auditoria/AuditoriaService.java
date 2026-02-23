package br.com.codenest.marknow.service.auditoria;

import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
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

    public void checkSuperAdmin() {
        if (!Boolean.TRUE.equals(ctx.superAdmin)) {
            throw BusinessException.of(ErrorCode.FORBIDDEN, "Acesso restrito ao administrador do sistema.");
        }
    }

    public void checkUserNotAdmin() {
        if (Boolean.TRUE.equals(ctx.superAdmin)) {
            throw BusinessException.of(ErrorCode.FORBIDDEN, "O administrador global não pode realizar operações de prestador.");
        }
    }

    public void checkRoleAdminPrestador() {
        checkUserNotAdmin();
        if (ctx.rolePrestador != PrestadorUsuarioRole.ADMIN) {
            throw BusinessException.of(ErrorCode.FORBIDDEN, "Ação permitida apenas para administradores da unidade.");
        }
    }

    public void checkRoleOperacionalOuAdmin() {
        checkUserNotAdmin();

        boolean isAuthorized = ctx.rolePrestador == PrestadorUsuarioRole.ADMIN ||
                ctx.rolePrestador == PrestadorUsuarioRole.OPERACIONAL;

        if (!isAuthorized) {
            throw BusinessException.of(ErrorCode.FORBIDDEN, "Ação permitida apenas para funcionários da unidade.");
        }
    }

    public String oidUsuarioLogadoOrNull() {
        return (ctx.usuario != null) ? ctx.usuario.getOid() : null;
    }
}