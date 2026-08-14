package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

import java.util.Optional;

public class FindUserByEmailUseCase {

    private final UserRepository repository;

    public FindUserByEmailUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> execute(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }

        return repository.findByEmail(email.toLowerCase().trim());
    }
}