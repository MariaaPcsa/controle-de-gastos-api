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
        if (requester.isAdmin()) {
            return repository.findAll();
        }
        return List.of(
                repository.findById(requester.getId())
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))
        );
    }
}
