package com.finance.analytics_service.domain.repository;


import com.finance.analytics_service.domain.model.ExpenseSummary;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository {

    ExpenseEntity save(ExpenseEntity expense);

    List<ExpenseEntity> findByUserId(UUID userId);

    ExpenseSummary getSummaryByUser(UUID userId);
}
