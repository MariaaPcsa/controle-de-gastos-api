package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

public class UpdateUserUseCase {

    private final UserRepository repository;

    public UpdateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(Long id, User data, User requester) {
        User existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 🔒 USER só pode alterar a si mesmo
        if (!requester.isAdmin() && !existing.getId().equals(requester.getId())) {
            throw new RuntimeException("Você não tem permissão para atualizar este usuário");
        }

        existing.setName(data.getName());
        existing.setEmail(data.getEmail());

        if (data.getPassword() != null) {
            existing.setPassword(data.getPassword());
        }

        // 🔒 SOMENTE ADMIN pode alterar TYPE
        if (requester.isAdmin() && data.getType() != null) {
            existing.setType(data.getType());
        }

        return repository.save(existing);
    }
}
