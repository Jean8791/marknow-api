package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.common.PageDTO;
import br.com.codenest.marknow.dto.convite.ConviteCreateRequest;
import br.com.codenest.marknow.dto.convite.ConviteResponse;
import br.com.codenest.marknow.service.ConviteService;
import br.com.codenest.marknow.service.EmailService;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;

@Path("/convite")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConviteResource {

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    ConviteService cadastroService;

    @POST
    @Operation(summary = "Cadastro")
    @APIResponse(responseCode = "201", description = "Criado com sucesso.", content = @Content(schema = @Schema(implementation = ConviteCreateRequest.class)))
    public Response create(@Valid ConviteCreateRequest req, @Context UriInfo uriInfo) {
        auditoriaService.checkSuperAdmin();
        ConviteResponse created = cadastroService.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getOid()).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    public PageDTO<ConviteResponse> list(@QueryParam("page") @DefaultValue("0") int page,
                                         @QueryParam("size") @DefaultValue("10") int size,
                                         @QueryParam("descricao") String descricao) {
        auditoriaService.checkSuperAdmin();
        return cadastroService.list(page, size, descricao);
    }
}
