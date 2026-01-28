package com.maria.finance.user.domain.usecase;


import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

public class DeleteUserUseCase {

    private final UserRepository repository;

    public DeleteUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public void execute(Long targetId, User requester) {
        User target = repository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!requester.isAdmin()) {
            throw new RuntimeException("Usuário não autorizado");
        }

        repository.delete(target);
    }
}

