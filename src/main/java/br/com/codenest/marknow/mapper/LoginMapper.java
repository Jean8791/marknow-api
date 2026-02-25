package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.auth.LoginResponse;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoginMapper {

    public LoginResponse toResponse(Usuario usuario, PrestadorUsuarioRole role, String token) {
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.getUsuario().setOid(usuario.getOid());
        resp.getUsuario().setNome(usuario.getNome());
        resp.getUsuario().setEmail(usuario.getEmail());
        resp.getUsuario().setPerfil(role);
        return resp;
    }

}
