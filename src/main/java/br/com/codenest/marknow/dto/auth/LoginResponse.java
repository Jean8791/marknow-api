package br.com.codenest.marknow.dto.auth;

import br.com.codenest.marknow.enums.LoginResponseRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private Usuario usuario = new Usuario();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Usuario {
        private String oid;
        private String nome;
        private String email;
        private String oidPrestador;
        private LoginResponseRole perfil;
    }
}
