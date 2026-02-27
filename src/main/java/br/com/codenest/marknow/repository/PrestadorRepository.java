package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.dto.prestador.PrestadorDetalhadoResponse;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.util.UtilsDocumento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrestadorRepository implements PanacheRepositoryBase<Prestador, String> {

    public PanacheQuery<PrestadorDetalhadoResponse> findDetailed(Sort sort) {
        return find("select " +
                "p.oid, " +
                "p.nome, " +
                "p.documento, " +
                "p.tipoPessoa, " +
                "p.status, " +
                "a.status, " +
                "a.fimPeriodoAtual, " +
                "a.provedor, " +
                "pl.nome, " +
                "pl.limiteAgendamentos, " +
                "pl.valor, " +
                "pl.possuiTrial, " +
                "pl.diasTrial " +
                "from Prestador p " +
                "left join Assinatura a on a.oidPrestador = p.oid " +
                "left join Plano pl on pl.oid = a.oidPlano", sort)
                .project(PrestadorDetalhadoResponse.class);
    }

    public boolean existsByDocumento(String novoDocumento) {
        return count("documento", novoDocumento) > 0;
    }

    public boolean existsByDocumentoEmOutroRegistro(String oidPrestador, String novoDocumento) {
        String novo = UtilsDocumento.apenasDigitosParaNull(novoDocumento);
        if (novo == null) return false;

        return count("documento = ?1 and oid <> ?2", novo, oidPrestador) > 0;
    }
}
