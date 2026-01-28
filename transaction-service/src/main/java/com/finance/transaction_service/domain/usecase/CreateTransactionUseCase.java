package com.finance.transaction_service.domain.usecase;




public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(Transaction transaction) {
        return repository.save(transaction);
    }
}

