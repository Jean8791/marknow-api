package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.convite.ConviteCreateRequest;
import br.com.codenest.marknow.dto.convite.ConviteResponse;
import br.com.codenest.marknow.entity.Convite;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConviteMapper {

    public Convite toEntity(ConviteCreateRequest req) {
        Convite convite = new Convite();
        convite.setDescricao(req.getDescricao());
        convite.setEmailDestinatario(req.getEmailDestinatario());
        convite.setExpiraEm(req.getExpiraEm());
        convite.setMaxUsos(req.getMaxUsos());
        return convite;
    }

    public ConviteResponse toResponse(Convite convite) {
        ConviteResponse planoResponse = new ConviteResponse();
        planoResponse.setOid(convite.getOid());
        planoResponse.setCodigo(convite.getCodigo());
        planoResponse.setDescricao(convite.getDescricao());
        planoResponse.setEmailDestinatario(convite.getEmailDestinatario());
        planoResponse.setExpiraEm(convite.getExpiraEm());
        planoResponse.setMaxUsos(convite.getMaxUsos());
        planoResponse.setQtdUsado(convite.getQtdUsado());
        planoResponse.setUtilizadoEm(convite.getUtilizadoEm());
        planoResponse.setUsuarioCriado(convite.getUsuarioCriacao() != null ? convite.getUsuarioCriacao().getNome() : null);
        return planoResponse;
    }
}
