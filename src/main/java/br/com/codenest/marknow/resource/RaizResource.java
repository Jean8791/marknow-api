package br.com.codenest.marknow.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.net.URI;

@Path("/")
@PermitAll
public class RaizResource {

    @GET
    @Operation(hidden = true)
    public Response raiz() {
        return Response.seeOther(URI.create("/q/swagger-ui/")).build();
    }
}
