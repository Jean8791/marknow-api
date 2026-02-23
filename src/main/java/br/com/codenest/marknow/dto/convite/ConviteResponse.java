package br.com.codenest.marknow.dto.convite;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class ConviteResponse {
    private String oid;
    private String codigo;
    private String emailDestinatario;
    private Instant expiraEm;
    private Instant utilizadoEm;
    private String usuarioCriado;
    private Integer maxUsos;
    private Integer qtdUsado;
}
