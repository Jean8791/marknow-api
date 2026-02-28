package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.assinatura.AssinaturaResponse;
import br.com.codenest.marknow.entity.Assinatura;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AssinaturaMapper {

    public AssinaturaResponse toResponse(Assinatura assinatura) {
        AssinaturaResponse ass = new AssinaturaResponse();

        ass.setOid(assinatura.getOid());
        ass.setPlano(assinatura.getPlano().getNome());
        ass.setStatus(assinatura.getStatus());
        ass.setFimPeriodoAtual(assinatura.getFimPeriodoAtual());
        ass.setToleranciaAte(assinatura.getToleranciaAte());
        ass.setProvedor(assinatura.getProvedor());
        ass.setReferenciaProvedor(assinatura.getReferenciaProvedor());
        ass.setCriadoPor(assinatura.getCriadoPor());
        ass.setAtualizadoPor(assinatura.getAtualizadoPor());
        ass.setCriadoEm(assinatura.getCriadoEm());
        ass.setAtualizadoEm(assinatura.getAtualizadoEm());

        return ass;
    }
}
