package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Assinatura;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AssinaturaRepository implements PanacheRepositoryBase<Assinatura, String> {
}
