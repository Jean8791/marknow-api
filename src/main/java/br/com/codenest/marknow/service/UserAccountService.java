package br.com.codenest.marknow.service;

import br.com.codenest.marknow.dto.user.UserCreateRequest;
import br.com.codenest.marknow.dto.user.UserResponse;
import br.com.codenest.marknow.dto.user.UserUpdateRequest;
import br.com.codenest.marknow.entity.UserAccount;
import br.com.codenest.marknow.exception.BusinessException;
import br.com.codenest.marknow.mapper.UserAccountMapper;
import br.com.codenest.marknow.repository.UserAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class UserAccountService {

    @Inject
    UserAccountRepository repo;

    @Inject
    UserAccountMapper mapper;

    @Transactional
    public UserResponse create(UserCreateRequest dto) {
        String normalizedEmail = dto.email.toLowerCase();

        if (repo.existsByEmail(normalizedEmail)) {
            throw BusinessException.conflict("EMAIL_ALREADY_EXISTS", "Já existe usuário cadastrado com este e-mail.");
        }
        UserAccount userAccount = mapper.toEntity(dto);
        repo.persist(userAccount);
        return mapper.toResponse(userAccount);
    }

    public UserResponse get(String oid) {
        UserAccount userAccount = repo.findById(oid);
        if (userAccount == null) {
            throw new NotFoundException("UserAccount not found");
        }
        return mapper.toResponse(userAccount);
    }

    public List<UserResponse> list(int page, int size) {
        return repo.findAll()
                .page(page, size)
                .list()
                .stream()
                .map(u -> mapper.toResponse(u))
                .toList();
    }

    @Transactional
    public UserResponse update(String oid, UserUpdateRequest req) {
        UserAccount userAccount = repo.findById(oid);

        if (userAccount == null) {
            throw new NotFoundException("UserAccount not found");
        }

        String normalizedEmail = req.email.toLowerCase();

        if (!normalizedEmail.equals(userAccount.email) && repo.existsByEmail(normalizedEmail)) {
            throw BusinessException.conflict("EMAIL_ALREADY_EXISTS", "Já existe usuário cadastrado com este e-mail.");
        }

        mapper.toUpdateEntity(userAccount, req);

        return mapper.toResponse(userAccount);
    }

    @Transactional
    public void delete(String oid) {
        boolean deleted = repo.deleteById(oid);
        if (!deleted) {
            throw new NotFoundException("UserAccount not found");
        }
    }
}
