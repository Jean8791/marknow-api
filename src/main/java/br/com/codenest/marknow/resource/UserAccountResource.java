package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.user.UserCreateRequest;
import br.com.codenest.marknow.dto.user.UserResponse;
import br.com.codenest.marknow.dto.user.UserUpdateRequest;
import br.com.codenest.marknow.service.UserAccountService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "UserAccount")
public class UserAccountResource {

    @Inject
    UserAccountService service;

    @POST
    @Operation(summary = "Criar usuário")
    public Response create(@Valid UserCreateRequest req, @Context UriInfo uriInfo) {
        UserResponse created = service.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.oid).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{oid}")
    @Operation(summary = "Buscar usuário por OID")
    public UserResponse get(@PathParam("oid") String oid) {
        return service.get(oid);
    }

    @GET
    @Operation(summary = "Listar usuários (paginado)")
    public List<UserResponse> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        return service.list(page, size);
    }

    @PUT
    @Path("/{oid}")
    @Operation(summary = "Atualizar usuário")
    public UserResponse update(@PathParam("oid") String oid, @Valid UserUpdateRequest req) {
        return service.update(oid, req);
    }

    @DELETE
    @Path("/{oid}")
    @Operation(summary = "Excluir usuário")
    public Response delete(@PathParam("oid") String oid) {
        service.delete(oid);
        return Response.noContent().build();
    }
}
