package com.finance.analytics_service.domain.usecase;



import java.time.LocalDate;

public class ProcessTransactionUseCase {

    private final ExpenseRepository repository;

    public ProcessTransactionUseCase(ExpenseRepository repository) {
        this.repository = repository;
    }

    public void execute(Long userId, String category, String type,
                        double amount, LocalDate date) {

        if (type.equals("DEPOSIT")) return; // regra de negócio

        repository.save(userId, category, type, amount, date);
    }
}

