package br.com.codenest.marknow.dto.prestador;

import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.util.UtilsString;
import br.com.codenest.marknow.validation.CpfOrCnpj;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 9)
    @NotBlank(message = "CEP e obrigatorio")
    private String cep;

    @Size(max = 150)
    @NotBlank(message = "Logradouro e obrigatorio")
    private String logradouro;

    @Size(max = 20)
    @NotBlank(message = "Numero e obrigatorio")
    private String numero;

    @Size(max = 100)
    @NotBlank(message = "Bairro e obrigatorio")
    private String bairro;

    @Size(max = 100)
    @NotBlank(message = "Cidade e obrigatorio")
    private String cidade;

    @Size(max = 2)
    @NotBlank(message = "UF e obrigatorio")
    private String uf;

    @Size(max = 100)
    @NotBlank(message = "Complemento e obrigatorio")
    private String complemento;

    private String logoUrl;

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

    @JsonSetter("cep")
    public void setCep(String cep) {
        this.cep = UtilsString.apararParaNull(cep);
    }

    @JsonSetter("logradouro")
    public void setLogradouro(String logradouro) {
        this.logradouro = UtilsString.apararParaNull(logradouro);
    }

    @JsonSetter("numero")
    public void setNumero(String numero) {
        this.numero = UtilsString.apararParaNull(numero);
    }

    @JsonSetter("bairro")
    public void setBairro(String bairro) {
        this.bairro = UtilsString.apararParaNull(bairro);
    }

    @JsonSetter("cidade")
    public void setCidade(String cidade) {
        this.cidade = UtilsString.apararParaNull(cidade);
    }

    @JsonSetter("uf")
    public void setUf(String uf) {
        this.uf = UtilsString.apararParaNull(uf);
    }

    @JsonSetter("complemento")
    public void setComplemento(String complemento) {
        this.complemento = UtilsString.apararParaNull(complemento);
    }

    @AssertTrue(message = "documento incompatível com tipoPessoa (PF=CPF, PJ=CNPJ)")
    public boolean isDocumentoCompativel() {
        if (this.tipoPessoa == null || this.documento == null || this.documento.isBlank()) return true;
        int len = this.documento.replaceAll("\\D", "").length();
        return (this.tipoPessoa == TipoPessoa.PF && len == 11) || (this.tipoPessoa == TipoPessoa.PJ && len == 14);
    }
}
