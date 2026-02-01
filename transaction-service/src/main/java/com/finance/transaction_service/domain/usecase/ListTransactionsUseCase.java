package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import java.util.List;

public class ListTransactionsUseCase {

    private final TransactionRepository repository;

    public ListTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> execute(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("UserId não pode ser nulo");
        }

        List<Transaction> transactions = repository.findByUserId(userId);

        // Regra opcional: ordenar por data (mais recente primeiro)
        transactions.sort((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));

        return transactions;
    }
}
