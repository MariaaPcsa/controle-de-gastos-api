package com.finance.transaction_service.domain.repository;

import com.finance.transaction_service.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(Long id);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserIdAndPeriod(Long userId, LocalDateTime start, LocalDateTime end);

    boolean existsById(Long id);

    void deleteById(Long id);
}
