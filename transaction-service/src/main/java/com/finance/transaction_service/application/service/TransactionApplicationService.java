package com.finance.transaction_service.application.service;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.usecase.CreateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.UpdateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.DeleteTransactionUseCase;
import com.finance.transaction_service.domain.usecase.ListTransactionsUseCase;
import com.finance.transaction_service.infrastructure.external.ExchangeRateClient;
import com.finance.transaction_service.presentation.dto.FilterTransactionDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionApplicationService {

    private final CreateTransactionUseCase createUseCase;
    private final UpdateTransactionUseCase updateUseCase;
    private final DeleteTransactionUseCase deleteUseCase;
    private final ListTransactionsUseCase listUseCase;
    private final ExchangeRateClient exchangeRateClient;

    public TransactionApplicationService(
            CreateTransactionUseCase createUseCase,
            UpdateTransactionUseCase updateUseCase,
            DeleteTransactionUseCase deleteUseCase,
            ListTransactionsUseCase listUseCase,
            ExchangeRateClient exchangeRateClient) {

        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.listUseCase = listUseCase;
        this.exchangeRateClient = exchangeRateClient;
    }

    // =========================================================
    // CREATE
    // =========================================================

    public Transaction create(
            UUID userId,
            String description,
            BigDecimal amount,
            String currency,
            String category,
            TransactionType type) {

        validateUserId(userId);

        validateTransactionData(
                description,
                amount,
                currency,
                category,
                type
        );

        BigDecimal convertedAmount =
                convertToBRL(amount, currency);

        Transaction transaction =
                Transaction.create(
                        userId,
                        description,
                        convertedAmount,
                        amount,
                        currency,
                        category,
                        type
                );

        return createUseCase.execute(transaction);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public Transaction update(
            UUID transactionId,
            String description,
            BigDecimal amount,
            BigDecimal originalAmount,
            String category,
            TransactionType type,
            String currency,
            UUID userId) {

        validateTransactionId(transactionId);
        validateUserId(userId);

        validateTransactionData(
                description,
                amount,
                currency,
                category,
                type
        );

        BigDecimal convertedAmount =
                convertToBRL(amount, currency);

        /*
         * originalAmount representa o valor informado
         * originalmente pelo usuário.
         *
         * Caso venha nulo, usamos amount.
         */
        BigDecimal finalOriginalAmount =
                originalAmount != null
                        ? originalAmount
                        : amount;

        return updateUseCase.execute(
                transactionId,
                description,
                convertedAmount,
                finalOriginalAmount,
                category,
                type,
                userId
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void delete(
            UUID transactionId,
            UUID userId) {

        validateTransactionId(transactionId);
        validateUserId(userId);

        deleteUseCase.execute(
                transactionId,
                userId
        );
    }

    // =========================================================
    // LIST
    // =========================================================

    public List<Transaction> list(UUID userId) {

        validateUserId(userId);

        return listUseCase.execute(userId);
    }

    // =========================================================
    // LIST + FILTER
    // =========================================================

    public List<Transaction> list(
            UUID userId,
            FilterTransactionDTO filter) {

        validateUserId(userId);

        if (filter == null) {
            return listUseCase.execute(userId);
        }

        List<Transaction> transactions =
                listUseCase.execute(userId);

        if (filter.hasCategory()) {

            transactions = transactions.stream()
                    .filter(transaction ->
                            transaction.getCategory() != null
                                    && transaction.getCategory()
                                    .equalsIgnoreCase(
                                            filter.getCategory()
                                    )
                    )
                    .toList();
        }

        if (filter.hasType()) {

            transactions = transactions.stream()
                    .filter(transaction ->
                            transaction.getType()
                                    == filter.getType()
                    )
                    .toList();
        }

        if (filter.getStartDate() != null) {

            transactions = transactions.stream()
                    .filter(transaction ->
                            transaction.getCreatedAt() != null
                                    && !transaction.getCreatedAt()
                                    .isBefore(
                                            filter.getStartDate()
                                    )
                    )
                    .toList();
        }

        if (filter.getEndDate() != null) {

            transactions = transactions.stream()
                    .filter(transaction ->
                            transaction.getCreatedAt() != null
                                    && !transaction.getCreatedAt()
                                    .isAfter(
                                            filter.getEndDate()
                                    )
                    )
                    .toList();
        }

        transactions = transactions.stream()
                .sorted((t1, t2) -> {

                    int comparison = 0;

                    if ("createdAt".equalsIgnoreCase(
                            filter.getSortBy())) {

                        if (t1.getCreatedAt() != null
                                && t2.getCreatedAt() != null) {

                            comparison =
                                    t1.getCreatedAt()
                                            .compareTo(
                                                    t2.getCreatedAt()
                                            );
                        }

                    } else if ("amount".equalsIgnoreCase(
                            filter.getSortBy())) {

                        comparison =
                                t1.getAmount()
                                        .compareTo(
                                                t2.getAmount()
                                        );
                    }

                    return "DESC".equalsIgnoreCase(
                            filter.getSortDirection()
                    )
                            ? -comparison
                            : comparison;
                })
                .toList();

        return transactions;
    }

    // =========================================================
    // VALIDAÇÕES
    // =========================================================

    private void validateUserId(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID é obrigatório"
            );
        }
    }

    private void validateTransactionId(UUID transactionId) {

        if (transactionId == null) {
            throw new IllegalArgumentException(
                    "Transaction ID é obrigatório"
            );
        }
    }

    private void validateTransactionData(
            String description,
            BigDecimal amount,
            String currency,
            String category,
            TransactionType type) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "O valor da transação deve ser maior que zero"
            );
        }

        if (description == null
                || description.isBlank()) {

            throw new IllegalArgumentException(
                    "Descrição é obrigatória"
            );
        }

        if (category == null
                || category.isBlank()) {

            throw new IllegalArgumentException(
                    "Categoria é obrigatória"
            );
        }

        if (currency == null
                || currency.isBlank()) {

            throw new IllegalArgumentException(
                    "Moeda é obrigatória"
            );
        }

        if (type == null) {

            throw new IllegalArgumentException(
                    "Tipo da transação é obrigatório"
            );
        }
    }

    private BigDecimal convertToBRL(
            BigDecimal amount,
            String currency) {

        if ("BRL".equalsIgnoreCase(currency)) {
            return amount;
        }

        return exchangeRateClient.convert(
                amount,
                currency,
                "BRL"
        );
    }
}