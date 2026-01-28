package com.finance.transaction_service.domain.repository;



import domain.model.Transaction;

import java.util.List;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findByUserId(Long userId);

    void delete(Long id);
}
