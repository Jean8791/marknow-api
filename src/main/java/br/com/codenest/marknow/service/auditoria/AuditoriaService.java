package br.com.codenest.marknow.service.auditoria;

import br.com.codenest.marknow.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuditoriaService {

    @Inject
    TenantContext ctx;

    public String oidUsuarioCriacaoOuErro() {
        if (ctx.usuario == null) throw new RuntimeException("Sem usuário no contexto");
        return ctx.usuario.getOid();
    }
}

