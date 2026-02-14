package br.com.codenest.marknow.dto.cadastro;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CadastroResponse {
    private String oid;
    private String token;
}
