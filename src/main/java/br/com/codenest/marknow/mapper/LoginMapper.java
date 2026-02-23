package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.auth.LoginResponse;
import br.com.codenest.marknow.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoginMapper {

    public LoginResponse toResponse(Usuario usuario, String token) {
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setNome(usuario.getNome());
        resp.setEmail(usuario.getEmail());
        return resp;
    }

}
