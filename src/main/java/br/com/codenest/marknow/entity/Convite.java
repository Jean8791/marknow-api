package br.com.codenest.marknow.entity;

import br.com.codenest.marknow.entity.base.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "convite")
public class Convite extends AbstractEntity {

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "email_destinatario", length = 120)
    private String emailDestinatario;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "utilizado_em")
    private Instant utilizadoEm;

    @Column(name = "oid_usuario_criacao", nullable = false, length = 36)
    private String oidUsuarioCriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_usuario_criacao", insertable = false, updatable = false, nullable = false)
    private Usuario usuarioCriacao;

    @Column(name = "max_usos", nullable = false)
    private Integer maxUsos;

    @Column(name = "qtd_usado", nullable = false)
    private Integer qtdUsado;

    @PrePersist
    void beforePersist() {
        if (this.maxUsos == null) this.maxUsos = 1;
        if (this.qtdUsado == null) this.qtdUsado = 0;
        if (this.expiraEm == null) this.expiraEm = Instant.now().plusSeconds(3600);
        if (this.usuarioCriacao != null) {
            this.oidUsuarioCriacao = this.usuarioCriacao.getOid();
        }
    }

    public boolean isValido() {
        return utilizadoEm == null &&
                Instant.now().isBefore(expiraEm) &&
                qtdUsado < maxUsos;
    }
}
