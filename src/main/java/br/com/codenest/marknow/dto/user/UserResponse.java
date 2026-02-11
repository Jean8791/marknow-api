package br.com.codenest.marknow.dto.user;

import br.com.codenest.marknow.enums.UserAccountStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "UserResponse")
public class UserResponse {
    public String oid;
    public String email;
    public String fullName;
    public UserAccountStatus status;
    public Instant createdAt;
    public Instant updatedAt;
}
