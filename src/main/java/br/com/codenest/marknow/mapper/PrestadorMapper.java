package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.prestador.PrestadorCreateRequest;
import br.com.codenest.marknow.dto.prestador.PrestadorResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorUpdateRequest;
import br.com.codenest.marknow.entity.Prestador;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrestadorMapper {


    public Prestador toEntity(PrestadorCreateRequest dto) {
        Prestador prestador = new Prestador();
        prestador.setNome(dto.getNome());
        prestador.setDocumento(dto.getDocumento());
        prestador.setTipoPessoa(dto.getTipoPessoa());
        return prestador;
    }

    public void toUpdateEntity(Prestador prestador, PrestadorUpdateRequest dto) {
        if (dto.getNome() != null) prestador.setNome(dto.getNome());
        if (dto.getDocumento() != null) prestador.setDocumento(dto.getDocumento());
        if (dto.getTipoPessoa() != null) prestador.setTipoPessoa(dto.getTipoPessoa());
    }

    public PrestadorResponse toResponse(Prestador prestador) {
        PrestadorResponse prestadorResponse = new PrestadorResponse();
        prestadorResponse.setOid(prestador.getOid());
        prestadorResponse.setNome(prestador.getNome());
        prestadorResponse.setDocumento(prestador.getDocumento());
        prestadorResponse.setTipoPessoa(prestador.getTipoPessoa());
        prestadorResponse.setStatus(prestador.getStatus());
        prestadorResponse.setCreatedAt(prestador.getCreatedAt());
        prestadorResponse.setUpdatedAt(prestador.getUpdatedAt());
        return prestadorResponse;
    }
}
