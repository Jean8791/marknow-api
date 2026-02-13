package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import br.com.codenest.marknow.dto.usuario.UsuarioResponse;
import br.com.codenest.marknow.dto.usuario.UsuarioUpdateResquest;
import br.com.codenest.marknow.service.UsuarioService;
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

@Path("/usuario")
@Tag(name = "Usuario")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @POST
    @Operation(summary = "Criar usuário")
    @APIResponse(responseCode = "201", description = "Criado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    public Response create(@Valid UsuarioCreateResquest req, @Context UriInfo uriInfo) {
        UsuarioResponse created = usuarioService.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.oid).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{oid}")
    @Operation(summary = "Buscar usuário por OID")
    @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    public UsuarioResponse get(@PathParam("oid") String oid) {
        return usuarioService.get(oid);
    }

    @GET
    @Operation(summary = "Listar usuários (paginado)")
    public List<UsuarioResponse> list(@QueryParam("page") @DefaultValue("0") int page,
                                      @QueryParam("size") @DefaultValue("20") int size,
                                      @QueryParam("sort") String sort) {
        return usuarioService.list(page, size, sort);
    }

    @PUT
    @Path("/{oid}")
    @Operation(summary = "Atualizar usuário")
    public UsuarioResponse update(@PathParam("oid") String oid, @Valid UsuarioUpdateResquest req) {
        return usuarioService.update(oid, req);
    }

    @DELETE
    @Path("/{oid}")
    @Operation(summary = "Excluir usuário")
    public Response delete(@PathParam("oid") String oid) {
        usuarioService.delete(oid);
        return Response.noContent().build();
    }

}
