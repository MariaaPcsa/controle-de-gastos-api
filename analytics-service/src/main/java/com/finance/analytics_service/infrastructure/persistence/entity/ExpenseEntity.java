package com.finance.analytics_service.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class ExpenseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private String category;
    private String type;
    private Double amount;
    private LocalDate date;
}

