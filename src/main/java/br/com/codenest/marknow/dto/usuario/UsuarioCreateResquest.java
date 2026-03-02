package br.com.codenest.marknow.dto.usuario;

import br.com.codenest.marknow.enums.PrestadorUsuarioRole;
import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.util.UtilsString;
import br.com.codenest.marknow.validation.CpfOrCnpj;
import br.com.codenest.marknow.validation.EmailStrict;
import br.com.codenest.marknow.validation.TelefoneBrasil;
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
@Schema(name = "UsuarioCreateResquest")
public class UsuarioCreateResquest {

    private String oidPrestador;

    @NotBlank
    @EmailStrict
    @Schema(required = true, example = "admin@prestador.com")
    private String email;

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(required = true, example = "Nome do prestador")
    private String nome;

    @NotNull(message = "tipoPessoa é obrigatório")
    private TipoPessoa tipoPessoa;

    @CpfOrCnpj
    @Schema(example = "12345678000199")
    private String documento;

    @NotBlank
    @Size(min = 2, max = 2, message = "DDD inválido")
    private String dddTelefone;

    @TelefoneBrasil(message = "Número de telefone fixo inválido")
    private String telefone;

    @Size(min = 2, max = 5, message = "DDD inválido")
    private String dddCelular;

    @TelefoneBrasil(message = "Número de celular inválido")
    private String celular;

    @NotBlank
    @Schema(required = true, example = "senha de acesso")
    private String senha;

    @NotBlank
    private String comfirmarSenha;

    private String imagem;

    private PrestadorUsuarioRole role;

    @JsonSetter("oidPrestador")
    public void setOidPrestador(String oidPrestador) {
        this.oidPrestador = UtilsString.apararParaNull(oidPrestador);
    }

    @JsonSetter("email")
    public void setEmail(String email) {
        this.email = UtilsString.apararParaNull(email);
    }

    @JsonSetter("nome")
    public void setNome(String nome) {
        this.nome = UtilsString.apararParaNull(nome);
    }

    @JsonSetter("senha")
    public void setSenha(String senha) {
        this.senha = UtilsString.apararParaNull(senha);
    }

    @JsonSetter("dddTelefone")
    public void setDddTelefone(String dddTelefone) {
        this.dddTelefone = UtilsString.apararParaNull(dddTelefone);
    }

    @JsonSetter("dddCelular")
    public void setDddCelular(String dddCelular) {
        this.dddCelular = UtilsString.apararParaNull(dddCelular);
    }


    @JsonSetter("role")
    public void setRole(PrestadorUsuarioRole role) {
        this.role = role == null ? PrestadorUsuarioRole.OPERACIONAL : role;
    }

    @AssertTrue(message = "documento incompatível com tipoPessoa (PF=CPF, PJ=CNPJ)")
    public boolean isDocumentoCompativel() {
        if (this.tipoPessoa == null || this.documento == null || this.documento.isBlank()) return true;
        int len = this.documento.replaceAll("\\D", "").length();
        return (this.tipoPessoa == TipoPessoa.PF && len == 11) || (this.tipoPessoa == TipoPessoa.PJ && len == 14);
    }

}
