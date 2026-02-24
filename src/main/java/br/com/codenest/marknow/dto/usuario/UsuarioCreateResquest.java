package br.com.codenest.marknow.dto.usuario;

import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.util.UtilsString;
import br.com.codenest.marknow.validation.EmailStrict;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "UsuarioCreateResquest")
public class UsuarioCreateResquest {

    private String oidPrestador;

    @NotBlank
    @EmailStrict
    @Schema(required = true, example = "admin@prestador.com")
    private String email;

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(required = true, example = "Nome do prestador")
    private String nome;

    @NotBlank
    @Schema(required = true, example = "senha de acesso")
    private String senha;

    private PrestadorUsuarioRole role;

    @JsonSetter("oidPrestador")
    public void setOidPrestador(String oidPrestador) {
        this.oidPrestador = UtilsString.apararParaNull(oidPrestador);
    }

    @JsonSetter("email")
    public void setEmail(String email) {
        this.email = UtilsString.apararParaNull(email);
    }

    @JsonSetter("nome")
    public void setNome(String nome) {
        this.nome = UtilsString.apararParaNull(nome);
    }

    @JsonSetter("senha")
    public void setSenha(String senha) {
        this.senha = UtilsString.apararParaNull(senha);
    }

    @JsonSetter("role")
    public void setRole(PrestadorUsuarioRole role) {
        this.role = role == null ? PrestadorUsuarioRole.OPERACIONAL : role;
    }


}
