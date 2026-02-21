package br.com.codenest.marknow.dto.cadastro;

import br.com.codenest.marknow.dto.prestador.PrestadorCreateRequest;
import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CadastroCreateRequest {

    @Valid
    @NotNull
    private PrestadorCreateRequest prestador;

    @Valid
    @NotNull
    private UsuarioCreateResquest usuario;

    @NotBlank
    private String oidPlano;
}
