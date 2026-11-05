package br.com.codenest.marknow.service.auth;

import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.UsuarioStatus;
import br.com.codenest.marknow.util.UtilsSenha;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Optional;

@ApplicationScoped
public class SuperAdminSenhaStartup {

    private static final Logger LOG = Logger.getLogger(SuperAdminSenhaStartup.class);
    private static final String OID_SUPER_ADMIN = "00000000-0000-0000-0000-000000000000";
    private static final String SENHA_PADRAO = "admin123";

    @ConfigProperty(name = "marknow.super-admin.senha")
    Optional<String> senha;

    @Transactional
    void aoIniciar(@Observes StartupEvent ev) {
        Usuario admin = Usuario.findById(OID_SUPER_ADMIN);
        if (admin == null) return;

        if (senha.isPresent() && !senha.get().isBlank()) {
            if (!UtilsSenha.verificar(senha.get(), admin.getSenha())) {
                admin.setSenha(UtilsSenha.criptografar(senha.get()));
                LOG.info("Senha do super admin atualizada a partir da configuração.");
            }
            admin.setStatus(UsuarioStatus.ATIVO);
            return;
        }

        if (LaunchMode.current() == LaunchMode.NORMAL
                && UsuarioStatus.ATIVO.equals(admin.getStatus())
                && UtilsSenha.verificar(SENHA_PADRAO, admin.getSenha())) {
            admin.setStatus(UsuarioStatus.BLOQUEADO);
            LOG.warn("Super admin com a senha padrão foi bloqueado. Defina MARKNOW_SUPER_ADMIN_SENHA para liberá-lo.");
        }
    }
}
