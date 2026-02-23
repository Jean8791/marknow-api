package br.com.codenest.marknow.dto.cadastro;

import br.com.codenest.marknow.dto.prestador.PrestadorCreateRequest;
import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import br.com.codenest.marknow.util.UtilsString;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CadastroCreateRequest {

    @NotBlank
    private String codigo;

    @Valid
    @NotNull
    private PrestadorCreateRequest prestador;

    @Valid
    @NotNull
    private UsuarioCreateResquest usuario;

    @NotBlank
    private String oidPlano;

    @JsonSetter("codigo")
    public void setCodigo(String codigo) {
        this.codigo = UtilsString.apararParaNull(codigo);
    }

    @JsonSetter("oidPlano")
    public void setOidPlano(String oidPlano) {
        this.oidPlano = UtilsString.apararParaNull(oidPlano);
    }
}
