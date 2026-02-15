package br.com.codenest.marknow.security;

import br.com.codenest.marknow.entity.Usuario;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class TenantContext {
    public String prestadorOid;
    public Usuario usuario;
    public Boolean superAdmin;
}
