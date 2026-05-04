package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteUserUseCase {

    private final UserRepository repository;

    public DeleteUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id, User requester) {

        if (requester == null) {
            throw new IllegalArgumentException("Requisição não autenticada");
        }

        if (!requester.isAdmin()) {
            throw new SecurityException("Apenas ADMIN pode deletar usuários");
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!user.isActive()) {
            return; // idempotência
        }

        user.setActive(false);
        repository.save(user);
    }
}