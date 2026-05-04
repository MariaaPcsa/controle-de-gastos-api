package com.maria.finance.user.domain.repository;

import com.maria.finance.user.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    // Contrato para delete lógico (soft-delete)
    void delete(User user);
}