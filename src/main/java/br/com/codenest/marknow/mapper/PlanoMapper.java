package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.plano.PlanoCreateRequest;
import br.com.codenest.marknow.dto.plano.PlanoResponse;
import br.com.codenest.marknow.dto.plano.PlanoUpdateRequest;
import br.com.codenest.marknow.entity.Plano;
import br.com.codenest.marknow.enums.PlanoStatus;
import br.com.codenest.marknow.util.UtilsString;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class PlanoMapper {

    public Plano toEntity(PlanoCreateRequest req) {
        Plano plano = new Plano();
        updateEntityFromDto(
                plano,
                req.getNome(),
                req.getLimiteAgendamentos(),
                req.getValor(),
                null,
                req.getPossuiTrial(),
                req.getDiasTrial()
        );
        return plano;
    }

    public void toUpdateEntity(Plano plano, PlanoUpdateRequest req) {
        updateEntityFromDto(
                plano,
                req.getNome(),
                req.getLimiteAgendamentos(),
                req.getValor(),
                req.getStatus(),
                req.getPossuiTrial(),
                req.getDiasTrial()
        );
    }

    public PlanoResponse toResponse(Plano plano) {
        PlanoResponse planoResponse = new PlanoResponse();
        planoResponse.setOid(plano.getOid());
        planoResponse.setNome(plano.getNome());
        planoResponse.setValor(plano.getValor());
        planoResponse.setStatus(plano.getStatus());
        planoResponse.setLimiteAgendamentos(plano.getLimiteAgendamentos());
        planoResponse.setPossuiTrial(plano.getPossuiTrial());
        planoResponse.setDiasTrial(plano.getDiasTrial());
        return planoResponse;
    }

    private void updateEntityFromDto(
            Plano plano,
            String nome,
            Integer limiteAgendamentos,
            BigDecimal valor,
            PlanoStatus status,
            Boolean possuiTrial,
            Integer diasTrial
    ) {
        if (UtilsString.temTexto(nome)) plano.setNome(nome);
        if (limiteAgendamentos != null) plano.setLimiteAgendamentos(limiteAgendamentos);
        if (valor != null) plano.setValor(valor);
        if (status != null) plano.setStatus(status);
        if (possuiTrial != null) plano.setPossuiTrial(possuiTrial);
        if (diasTrial != null) plano.setDiasTrial(diasTrial);
    }
}
