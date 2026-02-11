package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.company.CompanyCreateRequest;
import br.com.codenest.marknow.dto.company.CompanyResponse;
import br.com.codenest.marknow.dto.company.CompanyUpdateRequest;
import br.com.codenest.marknow.entity.Company;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.mapper.CompanyMapper;
import br.com.codenest.marknow.repository.CompanyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class CompanyService {

    @Inject
    CompanyRepository repo;

    @Inject
    CompanyMapper mapper;

    @Transactional
    public CompanyResponse create(CompanyCreateRequest req) {

        if (repo.existsByCnpj(req.cnpj)) {
            throw BusinessException.conflict("CNPJ_ALREADY_EXISTS", "Já existe empresa cadastrada com este CNPJ.");
        }

        Company e = mapper.toEntity(req);

        repo.persist(e);
        return mapper.toResponse(e);
    }

    public CompanyResponse get(String oid) {
        Company e = repo.findById(oid);
        if (e == null) {
            throw new NotFoundException("Company not found");
        }
        return mapper.toResponse(e);
    }

    public List<CompanyResponse> list(int page, int size) {
        return repo.findAll()
                .page(page, size)
                .list()
                .stream()
                .map(e -> mapper.toResponse(e))
                .toList();
    }

    @Transactional
    public CompanyResponse update(String oid, CompanyUpdateRequest dto) {
        Company company = repo.findById(oid);

        if (company == null) {
            throw new NotFoundException("Company not found");
        }

        if (dto.cnpj != null && !dto.cnpj.equals(company.cnpj) && repo.existsByCnpj(dto.cnpj)) {
            throw BusinessException.conflict("CNPJ_ALREADY_EXISTS", "Já existe empresa cadastrada com este CNPJ.");
        }

        mapper.toUpdateEntity(company, dto);

        return mapper.toResponse(company);
    }

    @Transactional
    public void delete(String oid) {
        boolean deleted = repo.deleteById(oid);
        if (!deleted) throw new NotFoundException("Company not found");
    }
}
