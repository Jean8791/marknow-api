package br.com.codenest.marknow.dto.prestador;

import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.enums.TipoPessoa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class PrestadorResponse {
    private String oid;
    private String nome;
    private String documento;
    private TipoPessoa tipoPessoa;
    private PrestadorStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
