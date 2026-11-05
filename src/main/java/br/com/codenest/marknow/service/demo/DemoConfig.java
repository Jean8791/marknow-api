package br.com.codenest.marknow.service.demo;

import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.exception.ErrorCode;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

@ApplicationScoped
public class DemoConfig {

    @ConfigProperty(name = "marknow.demo.email")
    Optional<String> email;

    public Optional<String> email() {
        return email.filter(e -> !e.isBlank()).map(e -> e.trim().toLowerCase());
    }

    public boolean isDemo(String emailUsuario) {
        return emailUsuario != null && email().map(e -> e.equalsIgnoreCase(emailUsuario.trim())).orElse(false);
    }

    public void bloquearSeDemo(String emailUsuario, String mensagem) {
        if (isDemo(emailUsuario)) {
            throw BusinessException.of(ErrorCode.FORBIDDEN, mensagem);
        }
    }
}
