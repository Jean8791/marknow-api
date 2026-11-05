package br.com.codenest.marknow.service.agenda;

import br.com.codenest.marknow.dto.agenda.ServicoRequest;
import br.com.codenest.marknow.dto.agenda.ServicoResponse;
import br.com.codenest.marknow.entity.Servico;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import br.com.codenest.marknow.repository.ServicoRepository;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ServicoService {

    @Inject
    ServicoRepository repo;

    @Inject
    AuditoriaService auditoriaService;

    public List<ServicoResponse> listar(boolean somenteAtivos) {
        String prestador = auditoriaService.oidPrestadorLogadoOrThrow();
        String filtro = somenteAtivos ? "oidPrestador = ?1 and ativo = true" : "oidPrestador = ?1";
        return repo.list(filtro, Sort.by("nome"), prestador).stream().map(this::toResponse).toList();
    }

    public ServicoResponse get(String oid) {
        return toResponse(buscar(oid));
    }

    @Transactional
    public ServicoResponse criar(ServicoRequest req) {
        AgendaRegras.validarDuracao(req.duracaoMinutos());
        Servico servico = new Servico();
        aplicar(servico, req);
        repo.persist(servico);
        return toResponse(servico);
    }

    @Transactional
    public ServicoResponse atualizar(String oid, ServicoRequest req) {
        AgendaRegras.validarDuracao(req.duracaoMinutos());
        Servico servico = buscar(oid);
        aplicar(servico, req);
        servico.setAtualizadoPor(auditoriaService.oidUsuarioLogadoOrThrow());
        return toResponse(servico);
    }

    @Transactional
    public void inativar(String oid) {
        Servico servico = buscar(oid);
        servico.setAtivo(false);
        servico.setAtualizadoPor(auditoriaService.oidUsuarioLogadoOrThrow());
    }

    Servico buscar(String oid) {
        Servico servico = repo.findByOidNoPrestador(oid, auditoriaService.oidPrestadorLogadoOrThrow());
        if (servico == null) throw BusinessException.of(ErrorCode.NOT_FOUND, "Serviço não encontrado.");
        return servico;
    }

    private void aplicar(Servico servico, ServicoRequest req) {
        servico.setNome(req.nome().trim());
        servico.setDuracaoMinutos(req.duracaoMinutos());
        servico.setValor(req.valor());
        if (req.ativo() != null) servico.setAtivo(req.ativo());
    }

    ServicoResponse toResponse(Servico s) {
        return new ServicoResponse(s.getOid(), s.getNome(), s.getDuracaoMinutos(), s.getValor(), s.getAtivo());
    }
}
