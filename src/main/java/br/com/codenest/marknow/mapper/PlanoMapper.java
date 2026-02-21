package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.plano.PlanoCreateRequest;
import br.com.codenest.marknow.dto.plano.PlanoResponse;
import br.com.codenest.marknow.dto.plano.PlanoUpdateRequest;
import br.com.codenest.marknow.entity.Plano;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlanoMapper {

    public Plano toEntity(PlanoCreateRequest req) {
        Plano plano = new Plano();
        plano.setNome(req.getNome());
        plano.setValor(req.getValor());
        plano.setLimiteAgendamentos(req.getLimiteAgendamentos());
        plano.setPossuiTrial(req.getPossuiTrial());
        plano.setDiasTrial(req.getDiasTrial());
        return plano;
    }

    public void toUpdateEntity(Plano plano, PlanoUpdateRequest req) {
        if (req.getNome() != null) plano.setNome(req.getNome());
        if (req.getValor() != null) plano.setValor(req.getValor());
        if (req.getLimiteAgendamentos() != null) plano.setLimiteAgendamentos(req.getLimiteAgendamentos());
        if (req.getPossuiTrial() != null) plano.setPossuiTrial(req.getPossuiTrial());
        if (req.getDiasTrial() != null) plano.setDiasTrial(req.getDiasTrial());
    }

    public PlanoResponse toResponse(Plano plano) {
        PlanoResponse planoResponse = new PlanoResponse();
        planoResponse.setOid(plano.getOid());
        planoResponse.setNome(plano.getNome());
        planoResponse.setValor(plano.getValor());
        planoResponse.setLimiteAgendamentos(plano.getLimiteAgendamentos());
        planoResponse.setPossuiTrial(plano.getPossuiTrial());
        planoResponse.setDiasTrial(plano.getDiasTrial());
        return planoResponse;
    }
}
