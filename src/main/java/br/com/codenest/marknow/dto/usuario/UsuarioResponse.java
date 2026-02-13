package br.com.codenest.marknow.dto.usuario;

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
    public String senha;
    public UsuarioStatus status;
    public Instant createdAt;
    public Instant updatedAt;
}
