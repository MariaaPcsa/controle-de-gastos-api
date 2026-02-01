package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;

public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    // Agora recebe um Transaction pronto, validado e com valor convertido
    public Transaction execute(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transação não pode ser nula");
        }

        // Persistência
        return repository.save(transaction);
    }
}
