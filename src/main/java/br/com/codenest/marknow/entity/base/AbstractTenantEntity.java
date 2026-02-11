package br.com.codenest.marknow.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractTenantEntity extends AbstractEntity {

    @Column(name = "oid_company", length = 36, nullable = false, updatable = false)
    protected String oidCompany;

}
