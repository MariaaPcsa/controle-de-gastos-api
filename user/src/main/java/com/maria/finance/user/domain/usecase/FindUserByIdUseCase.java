package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import com.maria.finance.user.infrastructure.exception.ResourceNotFoundException;

public class FindUserByIdUseCase {

    private final UserRepository repository;

    public FindUserByIdUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(Long id, User requester) {

        // 🔒 USER só pode acessar o próprio ID
        if (!requester.isAdmin() && !requester.getId().equals(id)) {
            throw new RuntimeException("Você não tem permissão para acessar este usuário");
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // 🔥 bloqueia acesso a usuário desativado
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Usuário está desativado");
        }

        return user;
    }
}

