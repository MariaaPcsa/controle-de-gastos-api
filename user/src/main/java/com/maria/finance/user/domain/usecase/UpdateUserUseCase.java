package com.maria.finance.user.domain.usecase;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UpdateUserUseCase {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserUseCase(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(Long id, User data, User requester) {

        User existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 🔒 USER só pode alterar a si mesmo
        if (!requester.isAdmin() && !existing.getId().equals(requester.getId())) {
            throw new RuntimeException("Você não tem permissão para atualizar este usuário");
        }

        // ✔️ Atualizações básicas
        if (data.getName() != null) {
            existing.setName(data.getName());
        }

        if (data.getEmail() != null) {
            existing.setEmail(data.getEmail());
        }

        // 🔐 CORREÇÃO PRINCIPAL (senha)
        if (data.getPassword() != null && !data.getPassword().isBlank()) {

            // evita recriptografar senha já criptografada
            if (!passwordEncoder.matches(data.getPassword(), existing.getPassword())) {
                existing.setPassword(passwordEncoder.encode(data.getPassword()));
            }
        }

        // 🔒 SOMENTE ADMIN pode alterar TYPE
        if (requester.isAdmin() && data.getType() != null) {
            existing.setType(data.getType());
        }

        return repository.save(existing);
    }
}