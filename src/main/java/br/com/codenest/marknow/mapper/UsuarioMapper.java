package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import br.com.codenest.marknow.dto.usuario.UsuarioResponse;
import br.com.codenest.marknow.dto.usuario.UsuarioUpdateResquest;
import br.com.codenest.marknow.entity.Usuario;
import br.com.codenest.marknow.enums.TipoPessoa;
import br.com.codenest.marknow.enums.UsuarioStatus;
import br.com.codenest.marknow.util.UtilsString;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioMapper {

    public Usuario toEntity(UsuarioCreateResquest dto) {
        Usuario usuario = new Usuario();
        updateEntityFromDto(
                usuario,
                dto.getNome(),
                dto.getEmail(),
                dto.getTipoPessoa(),
                dto.getDocumento(),
                dto.getDddTelefone(),
                dto.getTelefone(),
                dto.getDddCelular(),
                dto.getCelular(),
                dto.getImagem(),
                dto.getSenha(),
                null,
                null
        );
        return usuario;
    }

    public void toUpdateEntity(Usuario usuario, UsuarioUpdateResquest dto) {
        updateEntityFromDto(
                usuario,
                dto.getNome(),
                dto.getEmail(),
                dto.getTipoPessoa(),
                dto.getDocumento(),
                dto.getDddTelefone(),
                dto.getTelefone(),
                dto.getDddCelular(),
                dto.getCelular(),
                dto.getImagem(),
                null,
                dto.getStatus(),
                null
        );
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse usuarioResponse = new UsuarioResponse();

        usuarioResponse.setOid(usuario.getOid());
        usuarioResponse.setNome(usuario.getNome());
        usuarioResponse.setEmail(usuario.getEmail());
        usuarioResponse.setStatus(usuario.getStatus());
        usuarioResponse.setCreatedAt(usuario.getCriadoEm());
        usuarioResponse.setUpdatedAt(usuario.getAtualizadoEm());

        return usuarioResponse;
    }

    private void updateEntityFromDto(
            Usuario usuario,
            String nome,
            String email,
            TipoPessoa TipoPessoa,
            String documento,
            String dddTelefone,
            String telefone,
            String dddCelular,
            String celular,
            String imagem,
            String senha,
            UsuarioStatus status,
            Boolean admin
    ) {
        if (UtilsString.temTexto(nome)) usuario.setNome(nome);
        if (UtilsString.temTexto(email)) usuario.setEmail(email);
        if (UtilsString.temTexto(documento)) usuario.setDocumento(documento);
        if (UtilsString.temTexto(dddTelefone)) usuario.setDddTelefone(dddTelefone);
        if (UtilsString.temTexto(telefone)) usuario.setTelefone(telefone);
        if (UtilsString.temTexto(dddCelular)) usuario.setDddCelular(dddCelular);
        if (UtilsString.temTexto(celular)) usuario.setCelular(celular);
        if (UtilsString.temTexto(imagem)) usuario.setImagem(imagem);
        if (UtilsString.temTexto(senha)) usuario.setSenha(senha);
        if (TipoPessoa != null) usuario.setTipoPessoa(TipoPessoa);
        if (status != null) usuario.setStatus(status);
        if (admin != null) usuario.setAdmin(admin);
    }
}
