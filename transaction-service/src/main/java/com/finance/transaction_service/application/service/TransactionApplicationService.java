package com.finance.transaction_service.application.service;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.domain.usecase.CreateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.UpdateTransactionUseCase;
import com.finance.transaction_service.domain.usecase.DeleteTransactionUseCase;
import com.finance.transaction_service.domain.usecase.ListTransactionsUseCase;
import com.finance.transaction_service.infrastructure.external.ExchangeRateClient;
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

    // ================= DELETE =================
    public void delete(UUID transactionId) {
        deleteUseCase.execute(transactionId);
    }

    // ================= LIST =================
    public List<Transaction> list(Long userId) {
        return listUseCase.execute(userId);
    }

    // ================= LIST WITH PERIOD =================
    public List<Transaction> listByPeriod(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        // Optional: pode criar um UseCase específico se quiser
        return listUseCase.execute(userId)
                .stream()
                .filter(t -> !t.getCreatedAt().isBefore(start) && !t.getCreatedAt().isAfter(end))
                .toList();
    }
}