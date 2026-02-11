package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.company.CompanyCreateRequest;
import br.com.codenest.marknow.dto.company.CompanyResponse;
import br.com.codenest.marknow.dto.company.CompanyUpdateRequest;
import br.com.codenest.marknow.entity.Company;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CompanyMapper {

    public Company toEntity(CompanyCreateRequest dto) {
        if (dto == null) {
            return null;
        }

        Company company = new Company();
        company.setName(dto.name);
        company.setCnpj(dto.cnpj);
        return company;
    }

    public void toUpdateEntity(Company company, CompanyUpdateRequest dto) {
        if (dto == null) {
            return;
        }
        if (dto.getStatus() != null && !dto.getStatus().equals(company.getStatus())) {
            company.setStatus(dto.getStatus());
        }
        company.setName(dto.getName());
        company.setCnpj(dto.getCnpj());
    }

    public CompanyResponse toResponse(Company company) {
        CompanyResponse companyResponse = new CompanyResponse();
        companyResponse.setOid(company.getOid());
        companyResponse.setName(company.getName());
        companyResponse.setCnpj(company.getCnpj());
        companyResponse.setStatus(company.getStatus());
        companyResponse.setCreatedAt(company.getCreatedAt());
        companyResponse.setUpdatedAt(company.getUpdatedAt());
        return companyResponse;
    }


}
