package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.domain.repository.UserRepository;
import java.util.UUID;
public class UpdateUserRoleUseCase {

    private final UserRepository repository;

    public UpdateUserRoleUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User updateRole(UUID id, UserType newType, User requester) {

        if (requester == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }

        if (!requester.isAdmin()) {
            throw new RuntimeException("Apenas ADMIN pode alterar o tipo do usuário");
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Não é possível alterar role de usuário desativado");
        }

        user.setType(newType);

        return repository.save(user);
    }
}