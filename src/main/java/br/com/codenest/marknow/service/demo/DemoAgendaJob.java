package br.com.codenest.marknow.service.demo;

import br.com.codenest.marknow.entity.Agendamento;
import br.com.codenest.marknow.entity.Disponibilidade;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.entity.Profissional;
import br.com.codenest.marknow.entity.Servico;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.AgendamentoStatus;
import br.com.codenest.marknow.enums.WhatsappStatus;
import br.com.codenest.marknow.repository.AgendamentoRepository;
import br.com.codenest.marknow.repository.DisponibilidadeRepository;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import br.com.codenest.marknow.repository.ProfissionalRepository;
import br.com.codenest.marknow.repository.ServicoRepository;
import br.com.codenest.marknow.repository.UsuarioRepository;
import br.com.codenest.marknow.service.agenda.AgendaRegras;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class DemoAgendaJob {

    private static final Logger LOG = Logger.getLogger(DemoAgendaJob.class);
    private static final int DIAS_A_FRENTE = 7;
    private static final int MINIMO_POR_DIA = 2;
    private static final List<String> CLIENTES = List.of(
            "Ana Souza", "Bruno Lima", "Carla Mendes", "Diego Rocha", "Elisa Prado",
            "Fábio Nunes", "Gabriela Reis", "Henrique Alves", "Isabela Costa", "João Pedro Martins");

    @Inject
    DemoConfig demoConfig;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    PrestadorUsuarioRepository prestadorUsuarioRepository;

    @Inject
    ProfissionalRepository profissionalRepository;

    @Inject
    ServicoRepository servicoRepository;

    @Inject
    DisponibilidadeRepository disponibilidadeRepository;

    @Inject
    AgendamentoRepository agendamentoRepository;

    void aoIniciar(@Observes StartupEvent ev) {
        preencher();
    }

    @Scheduled(every = "1h", delayed = "5m")
    void periodicamente() {
        preencher();
    }

    @Transactional
    void preencher() {
        if (demoConfig.email().isEmpty()) return;
        Usuario demo = usuarioRepository.findByEmail(demoConfig.email().get());
        if (demo == null) return;
        PrestadorUsuario vinculo = prestadorUsuarioRepository.findByOidUsuario(demo.getOid());
        if (vinculo == null) return;

        String prestador = vinculo.getOidPrestador();
        List<Profissional> profissionais = profissionalRepository.list("oidPrestador = ?1 and ativo = true", prestador);
        List<Servico> servicos = servicoRepository.list("oidPrestador = ?1 and ativo = true", prestador);
        if (profissionais.isEmpty() || servicos.isEmpty()) return;

        LocalDate hoje = LocalDate.now(AgendaRegras.ZONA);
        LocalDateTime agora = LocalDateTime.now(AgendaRegras.ZONA);
        int criados = 0;
        int sequencia = 0;

        for (int d = 0; d < DIAS_A_FRENTE; d++) {
            LocalDate dia = hoje.plusDays(d);
            for (Profissional profissional : profissionais) {
                List<AgendaRegras.Janela> janelas = disponibilidadeRepository
                        .list("oidPrestador = ?1 and oidProfissional = ?2 and diaSemana = ?3",
                                prestador, profissional.getOid(), dia.getDayOfWeek().getValue()).stream()
                        .map((Disponibilidade j) -> new AgendaRegras.Janela(j.getHoraInicio(), j.getHoraFim()))
                        .toList();
                if (janelas.isEmpty()) continue;

                List<Agendamento> doDia = agendamentosDoDia(prestador, profissional.getOid(), dia);
                long ativos = doDia.stream().filter(a -> a.getStatus() == AgendamentoStatus.AGENDADO).count();
                if (ativos >= MINIMO_POR_DIA) continue;

                for (int n = 0; n < 3; n++) {
                    Servico servico = servicos.get((sequencia + n) % servicos.size());
                    List<LocalTime> livres = AgendaRegras.horariosLivres(dia, janelas, servico.getDuracaoMinutos(),
                            ocupados(agendamentosDoDia(prestador, profissional.getOid(), dia)), agora);
                    if (livres.isEmpty()) break;
                    LocalTime horario = livres.get((livres.size() * (n + 1)) / 4);
                    criar(prestador, demo.getOid(), profissional, servico, dia.atTime(horario), sequencia++);
                    criados++;
                }
            }
        }
        if (criados > 0) {
            LOG.infof("Agenda de demonstração: %d agendamentos criados para os próximos %d dias.", criados, DIAS_A_FRENTE);
        }
    }

    private List<Agendamento> agendamentosDoDia(String prestador, String oidProfissional, LocalDate dia) {
        Instant inicio = dia.atStartOfDay(AgendaRegras.ZONA).toInstant();
        Instant fim = dia.plusDays(1).atStartOfDay(AgendaRegras.ZONA).toInstant();
        return agendamentoRepository.list("oidPrestador = ?1 and oidProfissional = ?2 and inicio >= ?3 and inicio < ?4",
                prestador, oidProfissional, inicio, fim);
    }

    private List<AgendaRegras.Intervalo> ocupados(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .filter(a -> a.getStatus() == AgendamentoStatus.AGENDADO)
                .map(a -> new AgendaRegras.Intervalo(
                        LocalDateTime.ofInstant(a.getInicio(), AgendaRegras.ZONA),
                        LocalDateTime.ofInstant(a.getFim(), AgendaRegras.ZONA)))
                .toList();
    }

    private void criar(String prestador, String criadoPor, Profissional profissional, Servico servico, LocalDateTime inicio, int sequencia) {
        Agendamento a = new Agendamento();
        a.setOidPrestador(prestador);
        a.setCriadoPor(criadoPor);
        a.setOidProfissional(profissional.getOid());
        a.setOidServico(servico.getOid());
        a.setClienteNome(CLIENTES.get(sequencia % CLIENTES.size()));
        a.setClienteDdd("11");
        a.setClienteTelefone(String.format("9%08d", 10_000_000 + sequencia * 7_919 % 89_999_999));
        a.setInicio(inicio.atZone(AgendaRegras.ZONA).toInstant());
        a.setFim(inicio.plusMinutes(servico.getDuracaoMinutos()).atZone(AgendaRegras.ZONA).toInstant());
        a.setWhatsappStatus(sequencia % 2 == 0 ? WhatsappStatus.ENVIADO_MANUAL : WhatsappStatus.NAO_ENVIADO);
        agendamentoRepository.persist(a);
        agendamentoRepository.flush();
    }
}
