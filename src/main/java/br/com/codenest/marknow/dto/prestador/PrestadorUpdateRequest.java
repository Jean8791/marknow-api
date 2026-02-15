package br.com.codenest.marknow.dto.prestador;

import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.util.UtilsString;
import br.com.codenest.marknow.validation.CpfOrCnpj;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
public class PrestadorUpdateRequest {

    @NotBlank
    @Size(max = 36)
    @Schema(required = true, example = "OID do prestador")
    private String oid;

    @NotBlank
    @Size(min = 3, max = 120)
    @Schema(required = true, example = "Clínica Exemplo LTDA")
    private String nome;

    @CpfOrCnpj
    @Schema(example = "12345678000199")
    private String documento;

    @NotNull(message = "tipoPessoa é obrigatório")
    private TipoPessoa tipoPessoa;

    @JsonSetter("oid")
    public void setOid(String oid) {
        this.oid = UtilsString.apararParaNull(oid);
    }

    @JsonSetter("nome")
    public void setNome(String nome) {
        this.nome = UtilsString.apararParaNull(nome);
    }

    @JsonSetter("documento")
    public void setDocumento(String documento) {
        this.documento = UtilsString.apararParaNull(documento);
    }

    @AssertTrue(message = "documento incompatível com tipoPessoa (PF=CPF, PJ=CNPJ)")
    public boolean isDocumentoCompativel() {
        if (tipoPessoa == null || documento == null || documento.isBlank()) return true;

        int len = documento.length();
        return (tipoPessoa == TipoPessoa.PF && len == 11) || (tipoPessoa == TipoPessoa.PJ && len == 14);
    }
}
