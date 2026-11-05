package br.com.codenest.marknow.service.agenda;

import br.com.codenest.marknow.dto.agenda.DisponibilidadeDTO;
import br.com.codenest.marknow.dto.agenda.ProfissionalRequest;
import br.com.codenest.marknow.dto.agenda.ProfissionalResponse;
import br.com.codenest.marknow.entity.Disponibilidade;
import br.com.codenest.marknow.entity.Profissional;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.repository.DisponibilidadeRepository;
import br.com.codenest.marknow.repository.ProfissionalRepository;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import br.com.codenest.marknow.util.UtilsString;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProfissionalService {

    @Inject
    ProfissionalRepository repo;

    @Inject
    DisponibilidadeRepository disponibilidadeRepository;

    @Inject
    AuditoriaService auditoriaService;

    public List<ProfissionalResponse> listar(boolean somenteAtivos) {
        String prestador = auditoriaService.oidPrestadorLogadoOrThrow();
        String filtro = somenteAtivos ? "oidPrestador = ?1 and ativo = true" : "oidPrestador = ?1";
        List<Profissional> profissionais = repo.list(filtro, Sort.by("nome"), prestador);
        Map<String, List<DisponibilidadeDTO>> janelas = disponibilidadeRepository
                .list("oidPrestador", prestador).stream()
                .collect(Collectors.groupingBy(Disponibilidade::getOidProfissional,
                        Collectors.mapping(this::toDto, Collectors.toList())));
        return profissionais.stream()
                .map(p -> toResponse(p, ordenar(janelas.getOrDefault(p.getOid(), List.of()))))
                .toList();
    }

    public ProfissionalResponse get(String oid) {
        Profissional profissional = buscar(oid);
        return toResponse(profissional, disponibilidades(oid));
    }

    @Transactional
    public ProfissionalResponse criar(ProfissionalRequest req) {
        validarDisponibilidades(req.disponibilidades());
        Profissional profissional = new Profissional();
        aplicar(profissional, req);
        repo.persist(profissional);
        substituirDisponibilidades(profissional.getOid(), req.disponibilidades());
        return toResponse(profissional, disponibilidades(profissional.getOid()));
    }

    @Transactional
    public ProfissionalResponse atualizar(String oid, ProfissionalRequest req) {
        validarDisponibilidades(req.disponibilidades());
        Profissional profissional = buscar(oid);
        aplicar(profissional, req);
        profissional.setAtualizadoPor(auditoriaService.oidUsuarioLogadoOrThrow());
        if (req.disponibilidades() != null) {
            substituirDisponibilidades(oid, req.disponibilidades());
        }
        return toResponse(profissional, disponibilidades(oid));
    }

    @Transactional
    public void inativar(String oid) {
        Profissional profissional = buscar(oid);
        profissional.setAtivo(false);
        profissional.setAtualizadoPor(auditoriaService.oidUsuarioLogadoOrThrow());
    }

    Profissional buscar(String oid) {
        Profissional profissional = repo.findByOidNoPrestador(oid, auditoriaService.oidPrestadorLogadoOrThrow());
        if (profissional == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Profissional não encontrado.");
        return profissional;
    }

    List<AgendaRegras.Janela> janelasDoDia(String oidProfissional, int diaSemana) {
        return disponibilidadeRepository.list("oidProfissional = ?1 and oidPrestador = ?2 and diaSemana = ?3",
                        oidProfissional, auditoriaService.oidPrestadorLogadoOrThrow(), diaSemana).stream()
                .map(d -> new AgendaRegras.Janela(d.getHoraInicio(), d.getHoraFim()))
                .toList();
    }

    private List<DisponibilidadeDTO> disponibilidades(String oidProfissional) {
        return ordenar(disponibilidadeRepository.list("oidProfissional = ?1 and oidPrestador = ?2",
                        oidProfissional, auditoriaService.oidPrestadorLogadoOrThrow()).stream()
                .map(this::toDto)
                .toList());
    }

    private void substituirDisponibilidades(String oidProfissional, List<DisponibilidadeDTO> novas) {
        if (novas == null) return;
        disponibilidadeRepository.list("oidProfissional = ?1 and oidPrestador = ?2",
                oidProfissional, auditoriaService.oidPrestadorLogadoOrThrow()).forEach(disponibilidadeRepository::delete);
        disponibilidadeRepository.flush();
        for (DisponibilidadeDTO dto : novas) {
            Disponibilidade d = new Disponibilidade();
            d.setOidProfissional(oidProfissional);
            d.setDiaSemana(dto.diaSemana());
            d.setHoraInicio(dto.horaInicio());
            d.setHoraFim(dto.horaFim());
            disponibilidadeRepository.persist(d);
        }
    }

    private void validarDisponibilidades(List<DisponibilidadeDTO> lista) {
        if (lista == null) return;
        AgendaRegras.validarJanelas(
                lista.stream().map(DisponibilidadeDTO::diaSemana).toList(),
                lista.stream().map(d -> new AgendaRegras.Janela(d.horaInicio(), d.horaFim())).toList());
    }

    private void aplicar(Profissional profissional, ProfissionalRequest req) {
        profissional.setNome(req.nome().trim());
        profissional.setDddTelefone(UtilsString.temTexto(req.dddTelefone()) ? req.dddTelefone() : null);
        profissional.setTelefone(UtilsString.temTexto(req.telefone()) ? req.telefone() : null);
        if (req.ativo() != null) profissional.setAtivo(req.ativo());
    }

    private DisponibilidadeDTO toDto(Disponibilidade d) {
        return new DisponibilidadeDTO(d.getDiaSemana(), d.getHoraInicio(), d.getHoraFim());
    }

    private List<DisponibilidadeDTO> ordenar(List<DisponibilidadeDTO> lista) {
        return lista.stream()
                .sorted(Comparator.comparing(DisponibilidadeDTO::diaSemana).thenComparing(DisponibilidadeDTO::horaInicio))
                .toList();
    }

    ProfissionalResponse toResponse(Profissional p, List<DisponibilidadeDTO> disponibilidades) {
        return new ProfissionalResponse(p.getOid(), p.getNome(), p.getDddTelefone(), p.getTelefone(), p.getAtivo(), disponibilidades);
    }
}
