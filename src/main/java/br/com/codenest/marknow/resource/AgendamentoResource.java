package br.com.codenest.marknow.resource;

import br.com.codenest.marknow.dto.agenda.AgendamentoRequest;
import br.com.codenest.marknow.dto.agenda.AgendamentoResponse;
import br.com.codenest.marknow.dto.agenda.RemarcarRequest;
import br.com.codenest.marknow.dto.agenda.UsoAgendamentosResponse;
import br.com.codenest.marknow.service.agenda.AgendamentoService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Path("/agendamento")
@Tag(name = "Agendamento")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgendamentoResource {

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    AgendamentoService service;

    @GET
    @Operation(summary = "Listar agendamentos de um período (datas locais, até 31 dias)")
    public List<AgendamentoResponse> listar(@QueryParam("de") LocalDate de,
                                            @QueryParam("ate") LocalDate ate,
                                            @QueryParam("oidProfissional") String oidProfissional) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.listar(de, ate, oidProfissional);
    }

    @GET
    @Path("/horarios-livres")
    @Operation(summary = "Horários de início livres para um profissional e serviço numa data")
    public List<LocalTime> horariosLivres(@QueryParam("oidProfissional") String oidProfissional,
                                          @QueryParam("oidServico") String oidServico,
                                          @QueryParam("data") LocalDate data,
                                          @QueryParam("ignorar") String ignorarOid) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.horariosLivres(oidProfissional, oidServico, data, ignorarOid);
    }

    @GET
    @Path("/uso")
    @Operation(summary = "Uso da quota mensal de agendamentos do plano")
    public UsoAgendamentosResponse uso() {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.uso();
    }

    @POST
    @Operation(summary = "Criar agendamento")
    public Response criar(@Valid AgendamentoRequest req, @Context UriInfo uriInfo) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        AgendamentoResponse criado = service.criar(req);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(criado.oid()).build()).entity(criado).build();
    }

    @PUT
    @Path("/{oid}/remarcar")
    @Operation(summary = "Remarcar agendamento (horário e, opcionalmente, profissional)")
    public AgendamentoResponse remarcar(@PathParam("oid") String oid, @Valid RemarcarRequest req) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.remarcar(oid, req);
    }

    @POST
    @Path("/{oid}/cancelar")
    @Operation(summary = "Cancelar agendamento")
    public AgendamentoResponse cancelar(@PathParam("oid") String oid) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.cancelar(oid);
    }

    @POST
    @Path("/{oid}/whatsapp-enviado")
    @Operation(summary = "Registrar que a mensagem de WhatsApp foi enviada manualmente")
    public AgendamentoResponse whatsappEnviado(@PathParam("oid") String oid) {
        auditoriaService.checkRoleOperacionalOuAdmin();
        return service.marcarWhatsappEnviado(oid);
    }
}
