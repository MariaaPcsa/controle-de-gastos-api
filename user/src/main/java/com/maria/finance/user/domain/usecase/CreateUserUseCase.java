package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

public class CreateUserUseCase {

    private final UserRepository repository;

    public CreateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(User user) {
        // Verifica se o email já existe
        repository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email já cadastrado");
                });

        // Aqui você pode criptografar a senha antes de salvar
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(user);
    }
}

