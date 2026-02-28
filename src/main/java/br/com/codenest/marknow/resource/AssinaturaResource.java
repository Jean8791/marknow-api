package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.assinatura.AssinaturaResponse;
import br.com.codenest.marknow.dto.common.PageDTO;
import br.com.codenest.marknow.service.assinatura.AssinaturaService;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/assinatura")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssinaturaResource {

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    AssinaturaService assinaturaService;

    @GET
    public PageDTO<AssinaturaResponse> list(@QueryParam("page") @DefaultValue("0") int page,
                                            @QueryParam("size") @DefaultValue("10") int size,
                                            @QueryParam("sort") @DefaultValue("criadoEm") String sort) {
        auditoriaService.checkSuperAdmin();
        return assinaturaService.list(page, size, sort);
    }

}
