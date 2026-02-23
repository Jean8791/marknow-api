package br.com.codenest.marknow.repository;

import br.com.codenest.marknow.entity.Prestador;
import br.com.codenest.marknow.util.UtilsDocumento;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PrestadorRepository implements PanacheRepositoryBase<Prestador, String> {

    public List<Prestador> findAll(int page, int size, String sort) {
        Sort panacheSort = Sort.by(sort);
        return findAll(panacheSort).page(page, size).list();
    }

    public boolean existsByDocumento(String novoDocumento) {
        return count("documento", novoDocumento) > 0;
    }

    public boolean existsByDocumentoEmOutroRegistro(String oidPrestador, String novoDocumento) {
        String novo = UtilsDocumento.apenasDigitosParaNull(novoDocumento);
        if (novo == null) return false;

        return count("documento = ?1 and oid <> ?2", novo, oidPrestador) > 0;
    }
}
