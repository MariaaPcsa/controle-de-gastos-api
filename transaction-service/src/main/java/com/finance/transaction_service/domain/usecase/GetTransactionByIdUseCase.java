package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import java.util.UUID;

public class GetTransactionByIdUseCase {

    private final TransactionRepository repository;

    public GetTransactionByIdUseCase(
            TransactionRepository repository) {

        this.repository = repository;
    }

    public Transaction execute(
            UUID transactionId,
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

        Transaction transaction =
                repository.findById(transactionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Transação não encontrada"
                                )
                        );

        if (!transaction.getUserId().equals(userId)) {
            throw new SecurityException(
                    "Você não possui permissão para acessar esta transação"
            );
        }

        return transaction;
    }
}
