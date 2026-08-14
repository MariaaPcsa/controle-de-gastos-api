package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateTransactionUseCase {

    private final TransactionRepository repository;

    public UpdateTransactionUseCase(
            TransactionRepository repository) {

        this.repository = repository;
    }

    public Transaction execute(
            UUID transactionId,
            String description,
            BigDecimal amount,
            BigDecimal originalAmount,
            String category,
            TransactionType type,
            UUID userId) {

        if (transactionId == null) {
            throw new IllegalArgumentException(
                    "Transaction ID é obrigatório"
            );
        }

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID é obrigatório"
            );
        }

        Transaction existing =
                repository.findById(transactionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Transação não encontrada"
                                )
                        );

        // 🔐 REGRA DE PROPRIEDADE
        if (!existing.getUserId().equals(userId)) {
            throw new SecurityException(
                    "Você não possui permissão para atualizar esta transação"
            );
        }

        Transaction updated =
                existing.update(
                        description,
                        amount,
                        originalAmount,
                        category,
                        type
                );

        return repository.save(updated);
    }
}