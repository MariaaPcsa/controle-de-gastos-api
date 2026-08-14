package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateUserUseCase {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository repository,
                             PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new RuntimeException("Email inválido");
        }
    }

    public User execute(User user) {

        validateEmail(user.getEmail());

        repository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email já cadastrado");
                });

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Senha obrigatória");
        }

        // 🔐 ÚNICO PONTO DE CRIPTOGRAFIA
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(user);
    }
}