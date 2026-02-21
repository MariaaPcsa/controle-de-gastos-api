package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateTransactionUseCase {

    private final TransactionRepository repository;

    public UpdateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(UUID id,
                               String description,
                               BigDecimal amount,
                               BigDecimal originalAmount,
                               String category,
                               TransactionType type) {

        // Busca transação existente
        Transaction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        // Atualiza campos
        Transaction updated = existing.update(description, amount, originalAmount, category, type);

        // Salva e retorna
        return repository.save(updated);
    }
}