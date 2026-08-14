package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

import java.util.List;

public class ListUsersUseCase {

    private final UserRepository repository;

    public ListUsersUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> execute(User requester) {

        if (requester == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }

        // 🔥 ADMIN vê todos os usuários
        if (requester.isAdmin()) {
            return repository.findAll();
        }

        // 🔒 USER comum só pode ver ele mesmo
        User self = repository.findById(requester.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(self.getActive())) {
            throw new RuntimeException("Usuário desativado");
        }

        return List.of(self);
    }
}