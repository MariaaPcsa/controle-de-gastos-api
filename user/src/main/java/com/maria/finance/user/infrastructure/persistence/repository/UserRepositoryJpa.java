package com.maria.finance.user.infrastructure.persistence.repository;

import com.maria.finance.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositoryJpa extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
}
