package com.finance.transaction_service.domain.repository;

import com.finance.transaction_service.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    List<Transaction> findByUserId(UUID userId);

    List<Transaction> findByUserIdAndPeriod(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsById(UUID id);

    void deleteById(UUID id);
}