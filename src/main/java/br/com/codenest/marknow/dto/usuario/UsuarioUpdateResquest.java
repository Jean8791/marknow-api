package br.com.codenest.marknow.dto.usuario;

import br.com.codenest.marknow.enums.UsuarioStatus;
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
@Schema(name = "UserUpdateRequest")
public class UsuarioUpdateResquest {

    @NotBlank
    public String oid;

    @NotBlank
    @EmailStrict
    @Schema(required = true, example = "admin@prestador.com")
    public String email;

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(required = true, example = "Nome do prestador")
    public String nome;

    @NotBlank
    @Schema(required = true, example = "senha de acesso")
    public String senha;

    @Schema(example = "ATIVO, INATIVO, BLOQUEADO")
    public UsuarioStatus status;

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
}
