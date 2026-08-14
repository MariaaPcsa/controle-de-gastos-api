package com.finance.transaction_service.presentation.dto;

import com.finance.transaction_service.domain.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateTransactionDTO {

    @NotBlank(message = "A descrição da transação é obrigatória")
    private String description;

    @NotNull(message = "O valor da transação é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor da transação deve ser maior que zero")
    private BigDecimal amount;

    @NotBlank(message = "A moeda da transação é obrigatória")
    private String currency;

    @NotNull(message = "O tipo da transação é obrigatório")
    private TransactionType type;

    @NotBlank(message = "A categoria da transação é obrigatória")
    private String category;

    // ================= CONSTRUCTORS =================
    public UpdateTransactionDTO() {}

    public UpdateTransactionDTO(String description, BigDecimal amount,
                               String currency, TransactionType type, String category) {
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.category = category;
    }

    // ================= GETTERS & SETTERS =================
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "UpdateTransactionDTO{" +
                "description='" + description + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", type=" + type +
                ", category='" + category + '\'' +
                '}';
    }
}

