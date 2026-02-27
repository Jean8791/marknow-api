package br.com.codenest.marknow.dto.prestador;

import br.com.codenest.marknow.enums.AssinaturaStatus;
import br.com.codenest.marknow.enums.PrestadorStatus;
import br.com.codenest.marknow.enums.TipoPessoa;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class PrestadorDetalhadoResponse {

    private String oid;
    private String nome;
    private String documento;
    private TipoPessoa tipoPessoa;
    private PrestadorStatus status;

    private AssinaturaStatus statusAssinatura;
    private Instant fimPeriodoAtual;
    private String provedor;

    private String nomePlano;
    private Integer limiteAgendamentos;
    private BigDecimal valorPlano;
    private Boolean possuiTrial;
    private Integer diasTrial;

    public PrestadorDetalhadoResponse(String oid, String nome, String documento, TipoPessoa tipoPessoa, PrestadorStatus status, AssinaturaStatus statusAssinatura, Instant fimPeriodoAtual, String provedor, String nomePlano, Integer limiteAgendamentos, BigDecimal valorPlano, Boolean possuiTrial, Integer diasTrial) {
        this.oid = oid;
        this.nome = nome;
        this.documento = documento;
        this.tipoPessoa = tipoPessoa;
        this.status = status;
        this.statusAssinatura = statusAssinatura;
        this.fimPeriodoAtual = fimPeriodoAtual;
        this.provedor = provedor;
        this.nomePlano = nomePlano;
        this.limiteAgendamentos = limiteAgendamentos;
        this.valorPlano = valorPlano;
        this.possuiTrial = possuiTrial;
        this.diasTrial = diasTrial;
    }
}