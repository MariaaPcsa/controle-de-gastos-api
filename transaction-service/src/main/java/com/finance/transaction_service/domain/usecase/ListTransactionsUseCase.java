package com.finance.transaction_service.domain.usecase;


import java.util.List;

public class ListTransactionsUseCase {

    private final TransactionRepository repository;

    public ListTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> execute(Long userId) {
        return repository.findByUserId(userId);
    }
}

