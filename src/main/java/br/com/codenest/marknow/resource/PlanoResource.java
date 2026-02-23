package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.plano.PlanoCreateRequest;
import br.com.codenest.marknow.dto.plano.PlanoResponse;
import br.com.codenest.marknow.service.PlanoService;
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
import java.util.List;

@Path("/plano")
@Tag(name = "Plano")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlanoResource {

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    PlanoService planoService;

    @POST
    @Operation(summary = "Criar plano")
    @APIResponse(responseCode = "201", description = "Criada", content = @Content(schema = @Schema(implementation = PlanoResponse.class)))
    public Response create(@Valid PlanoCreateRequest req, @Context UriInfo uriInfo) {
        auditoriaService.checkSuperAdmin();
        PlanoResponse created = planoService.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getOid()).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{oid}")
    @Operation(summary = "Buscar prestador por OID")
    @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PlanoResponse.class)))
    public PlanoResponse get(@PathParam("oid") String oid) {
        auditoriaService.checkSuperAdmin();
        return planoService.get(oid);
    }

    @GET
    @Operation(summary = "Listar prestadores (paginado)")
    public List<PlanoResponse> list(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("20") int size,
                                    @QueryParam("sort") @DefaultValue("criadoEm") String sort) {
        auditoriaService.checkSuperAdmin();
        return planoService.list(page, size, sort);
    }
}
