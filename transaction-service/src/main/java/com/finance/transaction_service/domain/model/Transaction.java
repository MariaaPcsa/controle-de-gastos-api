package com.finance.transaction_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private final Long id;
    private final Long userId;
    private final String description;
    private final BigDecimal amount;          // valor convertido em BRL
    private final BigDecimal originalAmount;  // valor na moeda original
    private final String currency;            // moeda original
    private final TransactionType type;
    private final LocalDateTime createdAt;

    // Construtor completo
    public Transaction(Long id,
                       Long userId,
                       String description,
                       BigDecimal amount,
                       BigDecimal originalAmount,
                       String currency,
                       TransactionType type,
                       LocalDateTime createdAt) {

        validate(userId, description, amount, type);

        this.id = id;
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.originalAmount = originalAmount;
        this.currency = currency;
        this.type = type;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Criação de nova transação
    public static Transaction create(Long userId,
                                     String description,
                                     BigDecimal amount,        // convertido
                                     BigDecimal originalAmount,
                                     String currency,
                                     TransactionType type) {
        return new Transaction(null, userId, description, amount, originalAmount, currency, type, LocalDateTime.now());
    }

    // Atualização de transação
    public Transaction update(String description,
                              BigDecimal amount,
                              BigDecimal originalAmount,
                              String currency,
                              TransactionType type) {
        return new Transaction(this.id, this.userId, description, amount, originalAmount, currency, type, this.createdAt);
    }

    // Valor com sinal (positivo para receita, negativo para despesa)
    public BigDecimal getSignedAmount() {
        return type.isIncome() ? amount : amount.negate();
    }

    // Regras de validação
    private void validate(Long userId, String description, BigDecimal amount, TransactionType type) {
        if (userId == null) throw new IllegalArgumentException("UserId não pode ser nulo");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero");
        if (type == null) throw new IllegalArgumentException("Tipo da transação é obrigatório");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Descrição da transação é obrigatória");
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public String getCurrency() { return currency; }
    public TransactionType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
