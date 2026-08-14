package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class UpdateUserUseCase {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserUseCase(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(UUID id, User data, User requester) {

        if (requester == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }

        User existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 🔒 regra de acesso
        if (!requester.isAdmin() && !existing.getId().equals(requester.getId())) {
            throw new RuntimeException("Sem permissão");
        }

        if (data.getName() != null && !data.getName().isBlank()) {
            existing.setName(data.getName());
        }

        if (data.getEmail() != null && !data.getEmail().isBlank()) {
            existing.setEmail(data.getEmail().toLowerCase().trim());
        }

        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(data.getPassword()));
        }

        if (requester.isAdmin() && data.getType() != null) {
            existing.setType(data.getType());
        }

        return repository.save(existing);
    }
}