package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.plano.PlanoCreateRequest;
import br.com.codenest.marknow.dto.plano.PlanoResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorCreateRequest;
import br.com.codenest.marknow.service.PlanoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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

@Path("/plano")
@Tag(name = "Plano")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlanoResource {

    @Inject
    PlanoService planoService;

    @POST
    @Operation(summary = "Criar plano")
    @APIResponse(responseCode = "201", description = "Criada", content = @Content(schema = @Schema(implementation = PlanoResponse.class)))
    public Response create(@Valid PlanoCreateRequest req, @Context UriInfo uriInfo) {
        PlanoResponse created = planoService.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getOid()).build();
        return Response.created(location).entity(created).build();
    }
}
