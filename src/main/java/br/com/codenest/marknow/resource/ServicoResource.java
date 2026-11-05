package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.agenda.ServicoRequest;
import br.com.codenest.marknow.dto.agenda.ServicoResponse;
import br.com.codenest.marknow.service.agenda.ServicoService;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/servico")
@Tag(name = "Serviço")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServicoResource {

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    ServicoService service;

    @GET
    @Operation(summary = "Listar serviços")
    public List<ServicoResponse> listar(@QueryParam("ativos") @DefaultValue("false") boolean somenteAtivos) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.listar(somenteAtivos);
    }

    @GET
    @Path("/{oid}")
    @Operation(summary = "Buscar serviço")
    public ServicoResponse get(@PathParam("oid") String oid) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.get(oid);
    }

    @POST
    @Operation(summary = "Criar serviço (duração múltipla de 30 minutos)")
    public Response criar(@Valid ServicoRequest req, @Context UriInfo uriInfo) {
        auditoriaService.checkRoleAdminPrestador();
        ServicoResponse criado = service.criar(req);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(criado.oid()).build()).entity(criado).build();
    }

    @PUT
    @Path("/{oid}")
    @Operation(summary = "Atualizar serviço")
    public ServicoResponse atualizar(@PathParam("oid") String oid, @Valid ServicoRequest req) {
        auditoriaService.checkRoleAdminPrestador();
        return service.atualizar(oid, req);
    }

    @DELETE
    @Path("/{oid}")
    @Operation(summary = "Inativar serviço")
    public Response inativar(@PathParam("oid") String oid) {
        auditoriaService.checkRoleAdminPrestador();
        service.inativar(oid);
        return Response.noContent().build();
    }
}
