package br.com.codenest.marknow.dto.user;

import br.com.codenest.marknow.enums.UserAccountStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "UserUpdateRequest")
public class UserUpdateRequest {

    @NotBlank
    @Email
    @Schema(required = true, example = "admin@empresa.com")
    public String email;

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(required = true, example = "Admin da Empresa")
    public String fullName;

    @Schema(example = "ACTIVE")
    public UserAccountStatus status;
}
