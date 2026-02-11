package br.com.codenest.marknow.mapper;

import br.com.codenest.marknow.dto.user.UserCreateRequest;
import br.com.codenest.marknow.dto.user.UserResponse;
import br.com.codenest.marknow.dto.user.UserUpdateRequest;
import br.com.codenest.marknow.entity.UserAccount;
import br.com.codenest.marknow.enums.UserAccountStatus;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserAccountMapper {
    public UserAccount toEntity(UserCreateRequest dto) {
        if (dto == null) {
            return null;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(dto.getEmail().toLowerCase());
        userAccount.setFullName(dto.getFullName());
        userAccount.setPasswordHash(dto.getPassword());
        userAccount.setStatus(UserAccountStatus.ACTIVE);
        return userAccount;
    }

    public void toUpdateEntity(UserAccount userAccount, UserUpdateRequest dto) {
        if (dto == null) {
            return;
        }
        userAccount.setFullName(dto.getFullName());
        userAccount.setEmail(dto.getEmail().toLowerCase());
        userAccount.setStatus(dto.getStatus());
    }

    public UserResponse toResponse(UserAccount userAccount) {
        UserResponse userResponse = new UserResponse();
        userResponse.setOid(userAccount.getOid());
        userResponse.setFullName(userAccount.getFullName());
        userResponse.setEmail(userAccount.getEmail());
        userResponse.setStatus(userAccount.getStatus());
        userResponse.setCreatedAt(userAccount.getCreatedAt());
        userResponse.setUpdatedAt(userAccount.getUpdatedAt());
        return userResponse;
    }
}
