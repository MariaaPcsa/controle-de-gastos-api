package com.finance.analytics_service.domain.repository;


import com.finance.analytics_service.domain.model.ExpenseSummary;

public interface ExpenseRepository {

    ExpenseSummary getSummaryByUser(Long userId);
}
