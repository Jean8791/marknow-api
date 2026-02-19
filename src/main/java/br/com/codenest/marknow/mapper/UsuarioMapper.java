package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import br.com.codenest.marknow.dto.usuario.UsuarioResponse;
import br.com.codenest.marknow.dto.usuario.UsuarioUpdateResquest;
import br.com.codenest.marknow.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioMapper {

    public Usuario toEntity(UsuarioCreateResquest dto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setNome(dto.getNome());
        usuario.setCriadoPor("SYSTEM");
        return usuario;
    }

    public void toUpdateEntity(Usuario usuario, UsuarioUpdateResquest dto) {
        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getEmail() != null) usuario.setEmail(dto.getEmail());
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
}
