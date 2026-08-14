package com.finance.transaction_service.presentation.dto;

import com.finance.transaction_service.domain.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionRequestDTO {

    @NotNull(message = "O valor da transação é obrigatório")
    @DecimalMin(
            value = "0.01",
            message = "O valor da transação deve ser maior que zero"
    )
    private BigDecimal amount;

    @NotBlank(message = "A moeda da transação é obrigatória")
    private String currency;

    @NotNull(message = "O tipo da transação é obrigatório")
    private TransactionType type;

    @NotBlank(message = "A categoria da transação é obrigatória")
    private String category;

    @NotBlank(message = "A descrição da transação é obrigatória")
    private String description;

    // =========================================================
    // GETTERS
    // =========================================================

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}