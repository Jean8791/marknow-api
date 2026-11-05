package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractTenantEntity;
import br.com.codenest.marknow.enums.AgendamentoStatus;
import br.com.codenest.marknow.enums.WhatsappStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "agendamento")
public class Agendamento extends AbstractTenantEntity {

    @Column(name = "oid_profissional", nullable = false, length = 36)
    private String oidProfissional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_profissional", insertable = false, updatable = false)
    private Profissional profissional;

    @Column(name = "oid_servico", nullable = false, length = 36)
    private String oidServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_servico", insertable = false, updatable = false)
    private Servico servico;

    @Column(name = "cliente_nome", nullable = false, length = 120)
    private String clienteNome;

    @Column(name = "cliente_ddd", nullable = false, length = 2)
    private String clienteDdd;

    @Column(name = "cliente_telefone", nullable = false, length = 9)
    private String clienteTelefone;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @Column(name = "inicio", nullable = false)
    private Instant inicio;

    @Column(name = "fim", nullable = false)
    private Instant fim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AgendamentoStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "whatsapp_status", nullable = false, length = 20)
    private WhatsappStatus whatsappStatus;

    @Column(name = "cancelado_em")
    private Instant canceladoEm;

    @PrePersist
    void beforePersist() {
        if (status == null) status = AgendamentoStatus.AGENDADO;
        if (whatsappStatus == null) whatsappStatus = WhatsappStatus.NAO_ENVIADO;
    }
}
