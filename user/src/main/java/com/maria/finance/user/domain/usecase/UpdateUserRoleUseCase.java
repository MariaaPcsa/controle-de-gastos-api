package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.domain.repository.UserRepository;

public class UpdateUserRoleUseCase {

    private final UserRepository repository;

    public UpdateUserRoleUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User updateRole(Long id, UserType newType, User requester) {
        if (!requester.isAdmin()) {
            throw new RuntimeException("Apenas ADMIN pode alterar o tipo do usuário");
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setType(newType); // ✅ só muda o role

        return repository.save(user);
    }
}
