package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import br.com.codenest.marknow.service.auditoria.AuditoriaService;
import br.com.codenest.marknow.util.UtilsString;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@ApplicationScoped
public class PrestadorUsuarioService {

    @Inject
    AuditoriaService auditoriaService;

    @Inject
    PrestadorUsuarioRepository repo;

    @Transactional
    public void create(@NotBlank String oidUsuario, @Valid UsuarioCreateResquest req) {
        if (repo.findByOidPrestadorAndOidUsuario(req.getOidPrestador(), oidUsuario) != null) {
            return;
        }
        PrestadorUsuario pu = new PrestadorUsuario();
        if (UtilsString.temTexto(req.getOidPrestador())) {
            pu.setOidPrestador(req.getOidPrestador());
        } else {
            pu.setOidPrestador(auditoriaService.oidPrestadorLogadoOrThrow());
        }
        pu.setOidUsuario(oidUsuario);
        pu.setRole(req.getRole());
        repo.persist(pu);
    }
}
