package com.maria.finance.user.infrastructure.persistence.repository;

import com.maria.finance.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryJpa extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);
}