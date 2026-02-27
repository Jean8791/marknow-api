package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.auth.LoginResponse;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.LoginResponseRole;
import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoginMapper {

    public LoginResponse toResponse(Usuario usuario, String oidPrestador, PrestadorUsuarioRole role, String token) {
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);

        LoginResponse.Usuario dtoUser = resp.getUsuario();
        dtoUser.setOid(usuario.getOid());
        dtoUser.setNome(usuario.getNome());
        dtoUser.setEmail(usuario.getEmail());
        dtoUser.setOidPrestador(oidPrestador);
        dtoUser.setPerfil(definirPerfil(usuario, role));

        return resp;
    }

    private LoginResponseRole definirPerfil(Usuario usuario, PrestadorUsuarioRole role) {
        if (Boolean.TRUE.equals(usuario.getAdmin())) {
            return LoginResponseRole.SUPER_ADMIN;
        }

        if (role != null) {
            return LoginResponseRole.valueOf(role.name());
        }

        return LoginResponseRole.OPERACIONAL;
    }
}
