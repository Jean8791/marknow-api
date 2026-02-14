package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.cadastro.CadastroCreateRequest;
import br.com.codenest.marknow.dto.cadastro.CadastroResponse;
import br.com.codenest.marknow.entity.Assinatura;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.*;
import br.com.codenest.marknow.service.auth.AuthService;
import br.com.codenest.marknow.util.UtilsRandom;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class CadastroService {

    @Inject
    AuthService authService;

    @Transactional
    public CadastroResponse create(@Valid CadastroCreateRequest req) {

        Instant agora = Instant.now();

        // 1) valida email único (case-insensitive) - evita exception feia do banco
        String email = req.getUsuario().getEmail().trim().toLowerCase();
        boolean emailExiste = Usuario.find("lower(email) = ?1", email).count() > 0;
        if (emailExiste) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // 2) cria Prestador (tenant)
        Prestador prestador = new Prestador();
        prestador.setOid(UtilsRandom.uuid());
        prestador.setNome(req.getPrestador().getNome());
        prestador.setDocumento(req.getPrestador().getDocumento()); // normaliza se quiser
        prestador.setTipoPessoa(req.getPrestador().getTipoPessoa());
        prestador.setStatus(PrestadorStatus.ATIVO);

        // auditoria: no bootstrap você pode deixar criadoPor null e depois exigir NOT NULL via V4
        prestador.setCriadoPor(null);
        prestador.setCriadoEm(agora);
        prestador.setAtualizadoEm(agora);

        prestador.persist();

        // 3) cria Usuario (admin do prestador)
        Usuario usuario = new Usuario();
        usuario.setOid(UtilsRandom.uuid());
        usuario.setEmail(email);
        usuario.setNome(req.getUsuario().getNome());
        usuario.setStatus(UsuarioStatus.ATIVO);
        usuario.setAdmin(false);

        // TODO: hash de senha (não guarde puro)
        usuario.setSenha(req.getUsuario().getSenha());

        usuario.setCriadoPor(null);
        usuario.setCriadoEm(agora);
        usuario.setAtualizadoEm(agora);

        usuario.persist();

        // 4) vínculo ADMIN usuário x prestador
        PrestadorUsuario pu = new PrestadorUsuario();
        pu.setOid(UtilsRandom.uuid());
        pu.setPrestador(prestador);
        pu.setUsuario(usuario);
        pu.setRole(PrestadorUsuarioRole.ADMIN);
        pu.setStatus(PrestadorUsuarioStatus.ATIVO);

        pu.setCriadoPor(usuario.getOid());
        pu.setCriadoEm(agora);
        pu.setAtualizadoEm(agora);

        pu.persist();

        // 5) cria Assinatura TRIAL
        Assinatura assinatura = new Assinatura();
        assinatura.setOid(UtilsRandom.uuid());
        assinatura.setOidPrestador(prestador.getOid());
        assinatura.setPlano(req.getPlano());
        assinatura.setStatus(AssinaturaStatus.TRIAL);

        assinatura.setFimPeriodoAtual(agora.plus(req.getDiasTrial(), ChronoUnit.DAYS));
        assinatura.setToleranciaAte(null);

        assinatura.setProvedor("INTERNO");
        assinatura.setReferenciaProvedor(null);

        assinatura.setCriadoPor(usuario.getOid());
        assinatura.setCriadoEm(agora);
        assinatura.setAtualizadoEm(agora);

        assinatura.persist();

        // 6) gera token e responde
        String token = authService.gerarToken(usuario);

        CadastroResponse resp = new CadastroResponse();
        resp.setOid(prestador.getOid()); // aqui "oid" é do prestador
        resp.setToken(token);
        return resp;
    }
}
