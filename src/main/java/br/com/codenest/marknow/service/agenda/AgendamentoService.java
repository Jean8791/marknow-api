package br.com.codenest.marknow.service.agenda;

import br.com.codenest.marknow.dto.agenda.AgendamentoRequest;
import br.com.codenest.marknow.dto.agenda.AgendamentoResponse;
import br.com.codenest.marknow.dto.agenda.RemarcarRequest;
import br.com.codenest.marknow.dto.agenda.UsoAgendamentosResponse;
import br.com.codenest.marknow.entity.Agendamento;
import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.Profissional;
import br.com.codenest.marknow.entity.Servico;
import br.com.codenest.marknow.enums.AgendamentoStatus;
import br.com.codenest.marknow.enums.WhatsappStatus;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.repository.AgendamentoRepository;
import br.com.codenest.marknow.repository.AssinaturaRepository;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import br.com.codenest.marknow.util.UtilsString;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class AgendamentoService {

    private static final String SQLSTATE_EXCLUSAO = "23P01";
    private static final int MAX_DIAS_CONSULTA = 31;
    private static final int PERCENTUAL_ALERTA = 80;

    @Inject
    AgendamentoRepository repo;

    @Inject
    AssinaturaRepository assinaturaRepository;

    @Inject
    ProfissionalService profissionalService;

    @Inject
    ServicoService servicoService;

    @Inject
    AuditoriaService auditoriaService;

    public List<AgendamentoResponse> listar(LocalDate de, LocalDate ate, String oidProfissional) {
        if (de == null) de = LocalDate.now(AgendaRegras.ZONA);
        if (ate == null) ate = de;
        if (ate.isBefore(de) || ChronoUnit.DAYS.between(de, ate) > MAX_DIAS_CONSULTA) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "Período inválido (máximo de 31 dias).");
        }
        Instant inicio = de.atStartOfDay(AgendaRegras.ZONA).toInstant();
        Instant fim = ate.plusDays(1).atStartOfDay(AgendaRegras.ZONA).toInstant();
        String prestador = auditoriaService.oidPrestadorLogadoOrThrow();

        List<Agendamento> agendamentos = UtilsString.temTexto(oidProfissional)
                ? repo.list("oidPrestador = ?1 and inicio >= ?2 and inicio < ?3 and oidProfissional = ?4",
                Sort.by("inicio"), prestador, inicio, fim, oidProfissional)
                : repo.list("oidPrestador = ?1 and inicio >= ?2 and inicio < ?3",
                Sort.by("inicio"), prestador, inicio, fim);
        return agendamentos.stream().map(this::toResponse).toList();
    }

    public List<LocalTime> horariosLivres(String oidProfissional, String oidServico, LocalDate data, String ignorarOid) {
        if (data == null) throw BusinessException.of(ErrorCode.BAD_REQUEST, "Informe a data.");
        Profissional profissional = profissionalAtivo(oidProfissional);
        Servico servico = servicoAtivo(oidServico);
        return AgendaRegras.horariosLivres(
                data,
                profissionalService.janelasDoDia(profissional.getOid(), data.getDayOfWeek().getValue()),
                servico.getDuracaoMinutos(),
                ocupados(profissional.getOid(), data, ignorarOid),
                LocalDateTime.now(AgendaRegras.ZONA));
    }

    @Transactional
    public AgendamentoResponse criar(AgendamentoRequest req) {
        Profissional profissional = profissionalAtivo(req.oidProfissional());
        Servico servico = servicoAtivo(req.oidServico());
        validarHorario(profissional, servico, req.inicio(), null);
        validarQuota(YearMonth.from(req.inicio()));

        Agendamento agendamento = new Agendamento();
        agendamento.setOidProfissional(profissional.getOid());
        agendamento.setOidServico(servico.getOid());
        agendamento.setClienteNome(req.clienteNome().trim());
        agendamento.setClienteDdd(req.clienteDdd());
        agendamento.setClienteTelefone(req.clienteTelefone());
        agendamento.setObservacao(UtilsString.temTexto(req.observacao()) ? req.observacao().trim() : null);
        definirPeriodo(agendamento, req.inicio(), servico);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);

        repo.persist(agendamento);
        gravarComChecagemDeConflito();
        return toResponse(agendamento);
    }

    @Transactional
    public AgendamentoResponse remarcar(String oid, RemarcarRequest req) {
        Agendamento agendamento = buscarAtivo(oid);
        String oidProfissional = UtilsString.temTexto(req.oidProfissional()) ? req.oidProfissional() : agendamento.getOidProfissional();
        Profissional profissional = profissionalAtivo(oidProfissional);
        Servico servico = servicoService.buscar(agendamento.getOidServico());
        validarHorario(profissional, servico, req.inicio(), agendamento.getOid());

        YearMonth mesAtual = YearMonth.from(LocalDateTime.ofInstant(agendamento.getInicio(), AgendaRegras.ZONA));
        if (!mesAtual.equals(YearMonth.from(req.inicio()))) {
            validarQuota(YearMonth.from(req.inicio()));
        }

        agendamento.setOidProfissional(profissional.getOid());
        agendamento.setProfissional(profissional);
        definirPeriodo(agendamento, req.inicio(), servico);
        agendamento.setWhatsappStatus(WhatsappStatus.NAO_ENVIADO);
        agendamento.setAtualizadoPor(auditoriaService.oidUsuarioLogadoOrThrow());
        gravarComChecagemDeConflito();
        return toResponse(agendamento);
    }

    @Transactional
    public AgendamentoResponse cancelar(String oid) {
        Agendamento agendamento = buscarAtivo(oid);
        agendamento.setStatus(AgendamentoStatus.CANCELADO);
        agendamento.setCanceladoEm(Instant.now());
        agendamento.setWhatsappStatus(WhatsappStatus.NAO_ENVIADO);
        agendamento.setAtualizadoPor(auditoriaService.oidUsuarioLogadoOrThrow());
        return toResponse(agendamento);
    }

    @Transactional
    public AgendamentoResponse marcarWhatsappEnviado(String oid) {
        Agendamento agendamento = buscar(oid);
        agendamento.setWhatsappStatus(WhatsappStatus.ENVIADO_MANUAL);
        agendamento.setAtualizadoPor(auditoriaService.oidUsuarioLogadoOrThrow());
        return toResponse(agendamento);
    }

    public UsoAgendamentosResponse uso() {
        return uso(YearMonth.now(AgendaRegras.ZONA));
    }

    private UsoAgendamentosResponse uso(YearMonth mes) {
        String prestador = auditoriaService.oidPrestadorLogadoOrThrow();
        Instant inicio = mes.atDay(1).atStartOfDay(AgendaRegras.ZONA).toInstant();
        Instant fim = mes.plusMonths(1).atDay(1).atStartOfDay(AgendaRegras.ZONA).toInstant();
        long usados = repo.count("oidPrestador = ?1 and status = ?2 and inicio >= ?3 and inicio < ?4",
                prestador, AgendamentoStatus.AGENDADO, inicio, fim);

        Assinatura assinatura = assinaturaRepository.findByOidPrestador(prestador);
        Integer limite = assinatura != null && assinatura.getPlano() != null ? assinatura.getPlano().getLimiteAgendamentos() : null;
        if (limite == null || limite <= 0) {
            return new UsoAgendamentosResponse(usados, limite, 0, false, false);
        }
        int percentual = (int) Math.min(100, usados * 100 / limite);
        return new UsoAgendamentosResponse(usados, limite, percentual, percentual >= PERCENTUAL_ALERTA, usados >= limite);
    }

    private void validarQuota(YearMonth mes) {
        if (uso(mes).bloqueado()) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE,
                    "Limite de agendamentos do plano atingido neste mês. Fale com o administrador para mudar de plano.");
        }
    }

    private void validarHorario(Profissional profissional, Servico servico, LocalDateTime inicio, String ignorarOid) {
        if (inicio == null) throw BusinessException.of(ErrorCode.BAD_REQUEST, "Informe o horário.");
        if (!AgendaRegras.alinhadoAoSlot(inicio.toLocalTime())) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "O horário deve começar em :00 ou :30.");
        }
        if (!inicio.isAfter(LocalDateTime.now(AgendaRegras.ZONA))) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Não é possível agendar no passado.");
        }
        List<AgendaRegras.Janela> janelas = profissionalService.janelasDoDia(profissional.getOid(), inicio.getDayOfWeek().getValue());
        if (!AgendaRegras.cabeEmAlgumaJanela(inicio.toLocalTime(), servico.getDuracaoMinutos(), janelas)) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Horário fora da disponibilidade do profissional.");
        }
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());
        boolean conflito = ocupados(profissional.getOid(), inicio.toLocalDate(), ignorarOid).stream()
                .anyMatch(o -> o.inicio().isBefore(fim) && inicio.isBefore(o.fim()));
        if (conflito) {
            throw BusinessException.of(ErrorCode.CONFLICT, "O profissional já tem um agendamento nesse horário.");
        }
    }

    private List<AgendaRegras.Intervalo> ocupados(String oidProfissional, LocalDate data, String ignorarOid) {
        Instant inicio = data.atStartOfDay(AgendaRegras.ZONA).toInstant();
        Instant fim = data.plusDays(1).atStartOfDay(AgendaRegras.ZONA).toInstant();
        return repo.list("oidPrestador = ?1 and oidProfissional = ?2 and status = ?3 and inicio < ?4 and fim > ?5 and oid <> ?6",
                        auditoriaService.oidPrestadorLogadoOrThrow(), oidProfissional, AgendamentoStatus.AGENDADO,
                        fim, inicio, ignorarOid == null ? "" : ignorarOid).stream()
                .map(a -> new AgendaRegras.Intervalo(
                        LocalDateTime.ofInstant(a.getInicio(), AgendaRegras.ZONA),
                        LocalDateTime.ofInstant(a.getFim(), AgendaRegras.ZONA)))
                .toList();
    }

    private void gravarComChecagemDeConflito() {
        try {
            repo.flush();
        } catch (PersistenceException e) {
            if (violouExclusao(e)) {
                throw BusinessException.of(ErrorCode.CONFLICT, "O profissional já tem um agendamento nesse horário.");
            }
            throw e;
        }
    }

    private boolean violouExclusao(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && SQLSTATE_EXCLUSAO.equals(sql.getSQLState())) {
                return true;
            }
        }
        return false;
    }

    private void definirPeriodo(Agendamento agendamento, LocalDateTime inicio, Servico servico) {
        agendamento.setInicio(inicio.atZone(AgendaRegras.ZONA).toInstant());
        agendamento.setFim(inicio.plusMinutes(servico.getDuracaoMinutos()).atZone(AgendaRegras.ZONA).toInstant());
    }

    private Profissional profissionalAtivo(String oid) {
        Profissional profissional = profissionalService.buscar(oid);
        if (!Boolean.TRUE.equals(profissional.getAtivo())) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Profissional inativo.");
        }
        return profissional;
    }

    private Servico servicoAtivo(String oid) {
        Servico servico = servicoService.buscar(oid);
        if (!Boolean.TRUE.equals(servico.getAtivo())) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Serviço inativo.");
        }
        return servico;
    }

    private Agendamento buscar(String oid) {
        Agendamento agendamento = repo.findByOidNoPrestador(oid, auditoriaService.oidPrestadorLogadoOrThrow());
        if (agendamento == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Agendamento não encontrado.");
        return agendamento;
    }

    private Agendamento buscarAtivo(String oid) {
        Agendamento agendamento = buscar(oid);
        if (agendamento.getStatus() != AgendamentoStatus.AGENDADO) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE, "Este agendamento já foi cancelado.");
        }
        return agendamento;
    }

    private AgendamentoResponse toResponse(Agendamento a) {
        return new AgendamentoResponse(
                a.getOid(),
                a.getOidProfissional(),
                a.getProfissional() != null ? a.getProfissional().getNome() : null,
                a.getOidServico(),
                a.getServico() != null ? a.getServico().getNome() : null,
                a.getClienteNome(),
                a.getClienteDdd(),
                a.getClienteTelefone(),
                a.getObservacao(),
                LocalDateTime.ofInstant(a.getInicio(), AgendaRegras.ZONA),
                LocalDateTime.ofInstant(a.getFim(), AgendaRegras.ZONA),
                a.getStatus(),
                a.getWhatsappStatus());
    }
}
