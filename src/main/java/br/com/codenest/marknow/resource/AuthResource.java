package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.auth.LoginRequest;
import br.com.codenest.marknow.dto.auth.LoginResponse;
import br.com.codenest.marknow.service.auth.AuthService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest req) {
        return Response.ok(new LoginResponse(authService.validarAcesso(req))).build();
    }
}
