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
        return repository.findByEmail(email);
    }
}
