package com.finance.transaction_service.infrastructure.persistence.repository;

import com.finance.transaction_service.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepositoryJpa extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByUserId(Long userId);

    List<TransactionEntity> findByUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

}