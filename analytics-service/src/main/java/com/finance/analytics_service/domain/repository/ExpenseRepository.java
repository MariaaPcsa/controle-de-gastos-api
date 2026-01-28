package com.finance.analytics_service.domain.repository;


import com.finance.analytics_service.domain.model.ExpenseSummary;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository {

    void save(Long userId, String category, String type,
              double amount, LocalDate date);

    List<ExpenseSummary> summaryByCategory(Long userId);

    List<ExpenseSummary> summaryByMonth(Long userId, int year, int month);
}
