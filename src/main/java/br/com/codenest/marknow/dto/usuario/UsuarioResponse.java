package br.com.codenest.marknow.dto.usuario;

import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.enums.UsuarioStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "UsuarioResponse")
public class UsuarioResponse {
    public String oid;
    public String email;
    public String nome;
    private TipoPessoa tipoPessoa;
    private String documento;
    private String dddTelefone;
    private String telefone;
    private String dddCelular;
    private String celular;
    public UsuarioStatus status;
    private PrestadorUsuarioRole role;
    public Instant createdAt;
    public Instant updatedAt;
}
