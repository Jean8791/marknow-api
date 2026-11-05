package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.agenda.ProfissionalRequest;
import br.com.codenest.marknow.dto.agenda.ProfissionalResponse;
import br.com.codenest.marknow.service.agenda.ProfissionalService;
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

@Path("/profissional")
@Tag(name = "Profissional")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfissionalResource {

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    ProfissionalService service;

    @GET
    @Operation(summary = "Listar profissionais com a disponibilidade semanal")
    public List<ProfissionalResponse> listar(@QueryParam("ativos") @DefaultValue("false") boolean somenteAtivos) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.listar(somenteAtivos);
    }

    @GET
    @Path("/{oid}")
    @Operation(summary = "Buscar profissional")
    public ProfissionalResponse get(@PathParam("oid") String oid) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.get(oid);
    }

    @POST
    @Operation(summary = "Criar profissional (com janelas de disponibilidade)")
    public Response criar(@Valid ProfissionalRequest req, @Context UriInfo uriInfo) {
        auditoriaService.checkRoleAdminPrestador();
        ProfissionalResponse criado = service.criar(req);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(criado.oid()).build()).entity(criado).build();
    }

    @PUT
    @Path("/{oid}")
    @Operation(summary = "Atualizar profissional; se 'disponibilidades' vier, substitui as janelas")
    public ProfissionalResponse atualizar(@PathParam("oid") String oid, @Valid ProfissionalRequest req) {
        auditoriaService.checkRoleAdminPrestador();
        return service.atualizar(oid, req);
    }

    @DELETE
    @Path("/{oid}")
    @Operation(summary = "Inativar profissional (o histórico de agendamentos é mantido)")
    public Response inativar(@PathParam("oid") String oid) {
        auditoriaService.checkRoleAdminPrestador();
        service.inativar(oid);
        return Response.noContent().build();
    }
}
