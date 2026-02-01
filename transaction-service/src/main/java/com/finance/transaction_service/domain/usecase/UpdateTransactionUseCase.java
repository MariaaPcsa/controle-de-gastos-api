package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import java.math.BigDecimal;

public class UpdateTransactionUseCase {

    private final TransactionRepository repository;

    public UpdateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(Long id,
                               String description,
                               BigDecimal convertedAmount,
                               BigDecimal originalAmount,
                               String currency,
                               TransactionType type) {

        Transaction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        Transaction updated = existing.update(description, convertedAmount, originalAmount, currency, type);

        return repository.save(updated);
    }
}
