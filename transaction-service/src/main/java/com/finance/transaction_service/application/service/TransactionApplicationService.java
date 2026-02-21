package com.finance.transaction_service.application.service;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.usecase.CreateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.UpdateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.DeleteTransactionUseCase;
import com.finance.transaction_service.domain.usecase.ListTransactionsUseCase;
import com.finance.transaction_service.infrastructure.external.ExchangeRateClient;
import com.finance.transaction_service.presentation.dto.FilterTransactionDTO;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@EntityScan("com.finance.transaction_service")
@EnableJpaRepositories("com.finance.transaction_service")
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
            ExchangeRateClient exchangeRateClient
    ) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.listUseCase = listUseCase;
        this.exchangeRateClient = exchangeRateClient;
    }

    // ================= CREATE =================
    public Transaction create(Long userId,
                              String description,
                              BigDecimal amount,
                              String currency,
                              String category,
                              TransactionType type) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        // Converte valor se não for BRL
        BigDecimal convertedAmount = "BRL".equalsIgnoreCase(currency) ? amount
                : exchangeRateClient.convert(amount, currency, "BRL");

        // Cria transação
        Transaction transaction = Transaction.create(
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

    // ================= UPDATE =================
    public Transaction update(UUID transactionId,
                              String description,
                              BigDecimal amount,
                              BigDecimal originalAmount,
                              String category,
                              TransactionType type,
                              String currency) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        // Converte valor se não for BRL
        BigDecimal convertedAmount = "BRL".equalsIgnoreCase(currency) ? amount
                : exchangeRateClient.convert(amount, currency, "BRL");

        return updateUseCase.execute(
                transactionId,
                description,
                convertedAmount,
                originalAmount,
                category,
                type
        );
    }

    public Transaction update(UUID transactionId,
                              String description,
                              BigDecimal amount,
                              BigDecimal originalAmount,
                              String category,
                              TransactionType type,
                              String currency,
                              Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID é obrigatório");
        }

        return update(transactionId, description, amount, originalAmount, category, type, currency);
    }

    // ================= DELETE =================
    public void delete(UUID transactionId) {
        deleteUseCase.execute(transactionId);
    }

    public void delete(UUID transactionId, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID é obrigatório");
        }
        deleteUseCase.execute(transactionId);
    }

    // ================= LIST =================
    public List<Transaction> list(Long userId) {
        return listUseCase.execute(userId);
    }

    public List<Transaction> list(Long userId, FilterTransactionDTO filter) {
        List<Transaction> transactions = listUseCase.execute(userId);

        // Aplicar filtros
        if (filter.hasCategory()) {
            transactions = transactions.stream()
                    .filter(t -> t.getCategory().equalsIgnoreCase(filter.getCategory()))
                    .toList();
        }

        if (filter.hasType()) {
            transactions = transactions.stream()
                    .filter(t -> t.getType() == filter.getType())
                    .toList();
        }

        if (filter.getStartDate() != null) {
            transactions = transactions.stream()
                    .filter(t -> !t.getCreatedAt().isBefore(filter.getStartDate()))
                    .toList();
        }

        if (filter.getEndDate() != null) {
            transactions = transactions.stream()
                    .filter(t -> !t.getCreatedAt().isAfter(filter.getEndDate()))
                    .toList();
        }

        // Aplicar ordenação (básica)
        transactions = transactions.stream()
                .sorted((t1, t2) -> {
                    int comparison = 0;
                    if ("createdAt".equals(filter.getSortBy())) {
                        comparison = t1.getCreatedAt().compareTo(t2.getCreatedAt());
                    } else if ("amount".equals(filter.getSortBy())) {
                        comparison = t1.getAmount().compareTo(t2.getAmount());
                    }

                    return "DESC".equalsIgnoreCase(filter.getSortDirection()) ? -comparison : comparison;
                })
                .toList();

        return transactions;
    }
}