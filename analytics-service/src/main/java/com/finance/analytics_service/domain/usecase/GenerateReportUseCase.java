package com.finance.analytics_service.domain.usecase;



import java.util.List;

public class GenerateReportUseCase {

    private final ExpenseRepository repository;

    public GenerateReportUseCase(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<ExpenseSummary> byCategory(Long userId) {
        return repository.summaryByCategory(userId);
    }

    public List<ExpenseSummary> byMonth(Long userId, int year, int month) {
        return repository.summaryByMonth(userId, year, month);
    }
}
