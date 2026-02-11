package br.com.codenest.marknow.dto.company;

import br.com.codenest.marknow.enums.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CompanyResponse")
public class CompanyResponse {
    public String oid;
    public String name;
    public String cnpj;
    public CompanyStatus status;
    public Instant createdAt;
    public Instant updatedAt;
}
