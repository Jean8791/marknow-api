package br.com.codenest.marknow.dto.prestador;

import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.util.UtilsString;
import br.com.codenest.marknow.validation.CpfOrCnpj;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@NoArgsConstructor
public class PrestadorCreateRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    @Schema(required = true, example = "Clínica Exemplo LTDA")
    private String nome;

    @CpfOrCnpj
    @Schema(example = "12345678000199")
    private String documento;

    @NotNull(message = "tipoPessoa é obrigatório")
    private TipoPessoa tipoPessoa;

    @JsonSetter("nome")
    public void setNome(String nome) {
        this.nome = UtilsString.slugParaNull(nome);
    }

    @JsonSetter("documento")
    public void setDocumento(String documento) {
        this.documento = UtilsString.slugParaNull(documento);
    }

    @JsonSetter("tipoPessoa")
    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    @AssertTrue(message = "documento incompatível com tipoPessoa (PF=CPF, PJ=CNPJ)")
    public boolean isDocumentoCompativel() {
        if (tipoPessoa == null || documento == null || documento.isBlank()) return true;

        int len = documento.length();
        return (tipoPessoa == TipoPessoa.PF && len == 11) || (tipoPessoa == TipoPessoa.PJ && len == 14);
    }
}
