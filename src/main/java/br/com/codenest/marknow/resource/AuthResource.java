package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.auth.LoginRequest;
import br.com.codenest.marknow.dto.auth.LoginResponse;
import br.com.codenest.marknow.dto.cadastro.CadastroCreateRequest;
import br.com.codenest.marknow.dto.cadastro.CadastroResponse;
import br.com.codenest.marknow.service.CadastroService;
import br.com.codenest.marknow.service.auth.AuthService;
import jakarta.annotation.security.PermitAll;
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

import java.net.URI;

@Path("/auth")
@PermitAll
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    CadastroService cadastroService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest req) {
        LoginResponse loginResponse = authService.validarAcesso(req);
        return Response.ok(loginResponse).build();
    }

    @POST
    @Path("/cadastro")
    @Operation(summary = "Cadastro")
    @APIResponse(responseCode = "201", description = "Criado com sucesso.", content = @Content(schema = @Schema(implementation = CadastroCreateRequest.class)))
    public Response create(@Valid CadastroCreateRequest req, @Context UriInfo uriInfo) {
        CadastroResponse created = cadastroService.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getOid()).build();
        return Response.created(location).entity(created).build();
    }
}
