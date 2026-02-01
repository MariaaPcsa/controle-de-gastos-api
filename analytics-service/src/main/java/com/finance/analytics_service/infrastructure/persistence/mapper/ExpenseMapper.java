package com.finance.analytics_service.infrastructure.persistence.mapper;


import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;

public class ExpenseMapper {

    public static ExpenseEntity fromKafka(String payload) {
        // MOCK: depois você parseia JSON real do Kafka
        ExpenseEntity e = new ExpenseEntity();
        e.setUserId(1L);
        e.setDescription("Compra Mercado");
        e.setCategory("Alimentação");
        e.setAmount(new java.math.BigDecimal("50.00"));
        e.setDate(java.time.LocalDate.now());
        return e;
    }
}

