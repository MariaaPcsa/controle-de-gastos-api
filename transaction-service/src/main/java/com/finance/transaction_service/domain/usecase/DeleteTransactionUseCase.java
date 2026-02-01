package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import java.time.LocalDateTime;

public class DeleteTransactionUseCase {

    private final TransactionRepository repository;

    public DeleteTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public void execute(Long id) {

        // Regra 1: verificar se existe
        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));


        if (transaction.getCreatedAt().isBefore(LocalDateTime.now().minusDays(240))) {
             throw new IllegalStateException("Não é permitido deletar transações antigas");
         }

        repository.deleteById(id);
    }
}
