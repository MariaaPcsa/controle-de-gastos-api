package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

public class FindUserByIdUseCase {

    private final UserRepository repository;

    public FindUserByIdUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(Long id, User requester) {

        User found = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!requester.isAdmin() && !found.getId().equals(requester.getId())) {
            throw new RuntimeException("Você não tem permissão para acessar este usuário");
        }

        return found;
    }
}
