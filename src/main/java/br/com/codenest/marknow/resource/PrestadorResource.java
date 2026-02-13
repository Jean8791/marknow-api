package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.prestador.PrestadorCreateRequest;
import br.com.codenest.marknow.dto.prestador.PrestadorResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorUpdateRequest;
import br.com.codenest.marknow.service.PrestadorService;
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

@Path("/prestador")
@Tag(name = "Prestador")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrestadorResource {

    @Inject
    PrestadorService prestadorService;

    @POST
    @Operation(summary = "Criar prestador")
    @APIResponse(responseCode = "201", description = "Criada", content = @Content(schema = @Schema(implementation = PrestadorResponse.class)))
    public Response create(@Valid PrestadorCreateRequest req, @Context UriInfo uriInfo) {
        PrestadorResponse created = prestadorService.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getOid()).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{oid}")
    @Operation(summary = "Buscar prestador por OID")
    @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PrestadorResource.class)))
    public PrestadorResponse get(@PathParam("oid") String oid) {
        return prestadorService.get(oid);
    }

    @GET
    @Operation(summary = "Listar prestadores (paginado)")
    public List<PrestadorResponse> list(@QueryParam("page") @DefaultValue("0") int page,
                                        @QueryParam("size") @DefaultValue("20") int size,
                                        @QueryParam("sort") String sort) {
        return prestadorService.list(page, size, sort);
    }

    @PUT
    @Path("/{oid}")
    @Operation(summary = "Atualizar prestador")
    public PrestadorResponse update(@PathParam("oid") String oid, @Valid PrestadorUpdateRequest req) {
        return prestadorService.update(oid, req);
    }

    @DELETE
    @Path("/{oid}")
    @Operation(summary = "Excluir prestador")
    @APIResponse(responseCode = "204", description = "Sem conteúdo")
    public Response delete(@PathParam("oid") String oid) {
        prestadorService.delete(oid);
        return Response.noContent().build();
    }

}
