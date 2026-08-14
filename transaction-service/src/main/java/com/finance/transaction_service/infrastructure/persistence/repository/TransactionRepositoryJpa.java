package com.finance.transaction_service.infrastructure.persistence.repository;

import com.finance.transaction_service.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryJpa
        extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByUserId(UUID userId);

    List<TransactionEntity> findByUserIdAndCreatedAtBetween(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );
}
