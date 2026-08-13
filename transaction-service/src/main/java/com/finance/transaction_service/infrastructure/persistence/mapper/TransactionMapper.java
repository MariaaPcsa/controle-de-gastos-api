package com.finance.transaction_service.infrastructure.persistence.mapper;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.infrastructure.persistence.entity.TransactionEntity;

public final class TransactionMapper {

    private TransactionMapper() {
        // Classe utilitária
    }

    // =========================================================
    // DOMAIN -> ENTITY
    // =========================================================

    public static TransactionEntity toEntity(
            Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction não pode ser nula"
            );
        }

        TransactionEntity entity =
                new TransactionEntity();

        entity.setId(transaction.getId());
        entity.setUserId(transaction.getUserId());
        entity.setDescription(transaction.getDescription());
        entity.setAmount(transaction.getAmount());
        entity.setOriginalAmount(transaction.getOriginalAmount());
        entity.setCurrency(transaction.getCurrency());
        entity.setCategory(transaction.getCategory());
        entity.setType(transaction.getType());
        entity.setCreatedAt(transaction.getCreatedAt());

        return entity;
    }

    // =========================================================
    // ENTITY -> DOMAIN
    // =========================================================

    public static Transaction toDomain(
            TransactionEntity entity) {

        if (entity == null) {
            throw new IllegalArgumentException(
                    "TransactionEntity não pode ser nula"
            );
        }

        return Transaction.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getDescription(),
                entity.getAmount(),
                entity.getOriginalAmount(),
                entity.getCurrency(),
                entity.getCategory(),
                entity.getType(),
                entity.getCreatedAt()
        );
    }
}