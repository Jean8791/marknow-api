package br.com.codenest.marknow.service;

import br.com.codenest.marknow.entity.PrestadorUsuario;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.repository.PrestadorUsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;

@ApplicationScoped
public class PrestadorUsuarioService {

    @Inject
    PrestadorUsuarioRepository repo;

    public void create(@NotBlank String oidUsuario, @NotBlank String oidPrestador) {
        PrestadorUsuario pu = new PrestadorUsuario();
        pu.setOidPrestador(oidPrestador);
        pu.setOidUsuario(oidUsuario);
        pu.setRole(PrestadorUsuarioRole.ADMIN);
        repo.persist(pu);
    }
}
