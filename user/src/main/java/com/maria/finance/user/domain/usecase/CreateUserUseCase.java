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

        // ✅ valida email
        validateEmail(user.getEmail());

        // 🔒 verifica duplicidade
        repository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email já cadastrado");
                });

        // 🔐 CRIPTOGRAFIA (AQUI É O PONTO CRÍTICO)
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 💾 salva
        return repository.save(user);
    }
}