package com.finance.transaction_service.domain.repository;

import com.finance.transaction_service.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    // Salva ou atualiza uma transação
    Transaction save(Transaction transaction);

    // Busca transação por UUID
    Optional<Transaction> findById(UUID id);

    // Lista todas as transações de um usuário
    List<Transaction> findByUserId(Long userId);

    // Lista transações de um usuário em um período
    List<Transaction> findByUserIdAndPeriod(Long userId, LocalDateTime start, LocalDateTime end);

    // Verifica se existe transação pelo UUID
    boolean existsById(UUID id);

    // Deleta transação pelo UUID
    void deleteById(UUID id);
}