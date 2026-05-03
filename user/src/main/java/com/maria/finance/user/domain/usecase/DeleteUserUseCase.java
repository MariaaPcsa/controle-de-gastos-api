package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteUserUseCase {

    private final UserRepository repository;

    public DeleteUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public void execute(Long id, User requester) {
        if (requester == null) {
            throw new IllegalArgumentException("Requisição não autenticada");
        }

        if (!requester.isAdmin()) {
            throw new SecurityException("Apenas ADMIN pode deletar usuários");
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!user.isActive()) {
            // já inativo - idempotência
            return;
        }

        // delega a implementação do delete (soft-delete) ao repositório
        user.setActive(false);
        repository.save(user);
    }
}
