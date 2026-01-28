package com.finance.transaction_service.application.service;



import java.util.List;

public class TransactionApplicationService {

    private final CreateTransactionUseCase create;
    private final ListTransactionsUseCase list;

    public TransactionApplicationService(TransactionRepository repository) {
        this.create = new CreateTransactionUseCase(repository);
        this.list = new ListTransactionsUseCase(repository);
    }

    public Transaction create(Transaction transaction) {
        return create.execute(transaction);
    }

    public List<Transaction> list(Long userId) {
        return list.execute(userId);
    }
}

