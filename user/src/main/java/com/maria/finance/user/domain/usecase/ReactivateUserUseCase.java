package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

import java.util.UUID;

public class ReactivateUserUseCase {

    private final UserRepository repository;

    public ReactivateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(UUID id, User requester) {

        if (requester == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }

        if (!requester.isAdmin()) {
            throw new RuntimeException("Apenas ADMIN pode reativar usuários");
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Usuário já está ativo");
        }

        user.setActive(true);

        return repository.save(user);
    }
}