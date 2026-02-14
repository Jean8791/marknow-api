package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.cadastro.CadastroCreateRequest;
import br.com.codenest.marknow.dto.cadastro.CadastroResponse;
import br.com.codenest.marknow.service.CadastroService;
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

@Path("/cadastro")
@Tag(name = "Cadastro")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CadastroResource {

    @Inject
    CadastroService cadastroService;

    @POST
    @Operation(summary = "Cadastro")
    @APIResponse(responseCode = "201", description = "Criado com sucesso.", content = @Content(schema = @Schema(implementation = CadastroCreateRequest.class)))
    public Response create(@Valid CadastroCreateRequest req, @Context UriInfo uriInfo) {
        CadastroResponse created = cadastroService.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getOid()).build();
        return Response.created(location).entity(created).build();
    }
}
