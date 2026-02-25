package br.com.codenest.marknow.dto.auth;

import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
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
    public class Usuario {
        private String oid;
        private String nome;
        private String email;
        private PrestadorUsuarioRole perfil;
    }
}
