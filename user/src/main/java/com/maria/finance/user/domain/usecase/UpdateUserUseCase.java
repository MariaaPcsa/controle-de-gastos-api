package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

public class UpdateUserUseCase {

    private final UserRepository repository;

    public UpdateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(Long id, User userData, User requester) {

        User existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 🔒 USER só pode atualizar a si mesmo
        if (!requester.isAdmin() && !existing.getId().equals(requester.getId())) {
            throw new RuntimeException("Você não tem permissão para atualizar este usuário");
        }

        existing.setName(userData.getName());
        existing.setEmail(userData.getEmail());
        existing.setPassword(userData.getPassword());
        existing.setType(userData.getType());

        return repository.save(existing);
    }
}
