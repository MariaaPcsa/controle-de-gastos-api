package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeleteTransactionUseCase {

    private final TransactionRepository repository;

    public DeleteTransactionUseCase(
            TransactionRepository repository) {

        this.repository = repository;
    }

    public void execute(
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

        // 🔐 REGRA DE PROPRIEDADE
        if (!transaction.getUserId().equals(userId)) {
            throw new SecurityException(
                    "Você não possui permissão para deletar esta transação"
            );
        }

        // Regra de negócio existente
        if (transaction.getCreatedAt()
                .isBefore(LocalDateTime.now().minusDays(240))) {

            throw new IllegalStateException(
                    "Não é permitido deletar transações antigas"
            );
        }

        repository.deleteById(transactionId);
    }
}