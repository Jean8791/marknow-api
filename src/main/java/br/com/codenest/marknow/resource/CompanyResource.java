package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.company.CompanyCreateRequest;
import br.com.codenest.marknow.dto.company.CompanyResponse;
import br.com.codenest.marknow.dto.company.CompanyUpdateRequest;
import br.com.codenest.marknow.service.CompanyService;
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

@Path("/companies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Company")
public class CompanyResource {

    @Inject
    CompanyService service;

    @POST
    @Operation(summary = "Criar empresa")
    @APIResponse(responseCode = "201", description = "Criada",
            content = @Content(schema = @Schema(implementation = CompanyResponse.class)))
    public Response create(@Valid CompanyCreateRequest req, @Context UriInfo uriInfo) {
        CompanyResponse created = service.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.oid).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{oid}")
    @Operation(summary = "Buscar empresa por OID")
    @APIResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = CompanyResponse.class)))
    public CompanyResponse get(@PathParam("oid") String oid) {
        return service.get(oid);
    }

    @GET
    @Operation(summary = "Listar empresas (paginado)")
    public List<CompanyResponse> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.list(page, size);
    }

    @PUT
    @Path("/{oid}")
    @Operation(summary = "Atualizar empresa")
    public CompanyResponse update(@PathParam("oid") String oid, @Valid CompanyUpdateRequest req) {
        return service.update(oid, req);
    }

    @DELETE
    @Path("/{oid}")
    @Operation(summary = "Excluir empresa")
    @APIResponse(responseCode = "204", description = "Sem conteúdo")
    public Response delete(@PathParam("oid") String oid) {
        service.delete(oid);
        return Response.noContent().build();
    }
}
