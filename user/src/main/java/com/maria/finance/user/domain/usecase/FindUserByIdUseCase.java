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
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.isActive()) {
            throw new RuntimeException("Usuário está desativado");
        }

        return user;
    }

}
