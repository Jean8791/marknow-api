package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.prestador.PrestadorCreateRequest;
import br.com.codenest.marknow.dto.prestador.PrestadorResponse;
import br.com.codenest.marknow.dto.prestador.PrestadorUpdateRequest;
import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.util.UtilsString;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrestadorMapper {

    public Prestador toEntity(PrestadorCreateRequest dto) {
        Prestador prestador = new Prestador();
        updateEntityFromDto(
                prestador,
                dto.getNome(),
                dto.getDocumento(),
                dto.getTipoPessoa(),
                dto.getEmail(),
                dto.getDddTelefone(),
                dto.getTelefone(),
                dto.getDddCelular(),
                dto.getCelular(),
                dto.getCep(),
                dto.getLogradouro(),
                dto.getNumero(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getUf(),
                dto.getComplemento(),
                dto.getLogoUrl()
        );
        return prestador;
    }

    public void toUpdateEntity(Prestador prestador, PrestadorUpdateRequest dto) {
        updateEntityFromDto(
                prestador,
                dto.getNome(),
                dto.getDocumento(),
                dto.getTipoPessoa(),
                dto.getEmail(),
                dto.getDddTelefone(),
                dto.getTelefone(),
                dto.getDddCelular(),
                dto.getCelular(),
                dto.getCep(),
                dto.getLogradouro(),
                dto.getNumero(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getUf(),
                dto.getComplemento(),
                dto.getLogoUrl()
        );
    }

    public PrestadorResponse toResponse(Prestador p) {
        PrestadorResponse res = new PrestadorResponse();
        res.setOid(p.getOid());
        res.setNome(p.getNome());
        res.setDocumento(p.getDocumento());
        res.setTipoPessoa(p.getTipoPessoa());
        res.setStatus(p.getStatus());
        res.setCreatedAt(p.getCriadoEm());
        res.setUpdatedAt(p.getAtualizadoEm());

        res.setEmail(p.getEmail());
        res.setDddTelefone(p.getDddTelefone());
        res.setTelefone(p.getTelefone());
        res.setDddCelular(p.getDddCelular());
        res.setCelular(p.getCelular());

        res.setCep(p.getCep());
        res.setLogradouro(p.getLogradouro());
        res.setNumero(p.getNumero());
        res.setBairro(p.getBairro());
        res.setCidade(p.getCidade());
        res.setUf(p.getUf());
        res.setComplemento(p.getComplemento());
        res.setLogoUrl(p.getLogoUrl());
        return res;
    }

    private void updateEntityFromDto(
            Prestador p,
            String nome,
            String doc,
            TipoPessoa tp,
            String email,
            String dddTelefone,
            String telefone,
            String dddCelular,
            String celular,
            String cep,
            String log,
            String num,
            String bai,
            String cid,
            String uf,
            String comp,
            String logo
    ) {
        if (UtilsString.temTexto(nome)) p.setNome(nome);
        if (UtilsString.temTexto(doc)) p.setDocumento(doc);
        if (tp != null) p.setTipoPessoa(tp);
        if (UtilsString.temTexto(email)) p.setEmail(email);
        if (UtilsString.temTexto(dddTelefone)) p.setDddTelefone(dddTelefone);
        if (UtilsString.temTexto(telefone)) p.setTelefone(telefone);
        if (UtilsString.temTexto(dddCelular)) p.setDddCelular(dddCelular);
        if (UtilsString.temTexto(celular)) p.setCelular(celular);
        if (UtilsString.temTexto(cep)) p.setCep(cep);
        if (UtilsString.temTexto(log)) p.setLogradouro(log);
        if (UtilsString.temTexto(num)) p.setNumero(num);
        if (UtilsString.temTexto(bai)) p.setBairro(bai);
        if (UtilsString.temTexto(cid)) p.setCidade(cid);
        if (UtilsString.temTexto(uf)) p.setUf(uf);
        if (UtilsString.temTexto(comp)) p.setComplemento(comp);
        if (UtilsString.temTexto(logo)) p.setLogoUrl(logo);
    }
}
