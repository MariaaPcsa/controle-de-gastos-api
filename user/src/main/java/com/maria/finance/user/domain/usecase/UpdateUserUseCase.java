package com.maria.finance.user.domain.usecase;


import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

public class UpdateUserUseCase {

    private final UserRepository repository;

    public UpdateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(Long targetId, User updatedData, User requester) {

        User existing = repository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!requester.isAdmin() && !requester.getId().equals(targetId)) {
            throw new RuntimeException("Usuário não autorizado");
        }

        existing.setName(updatedData.getName());
        existing.setEmail(updatedData.getEmail());

        return repository.save(existing);
    }
}

