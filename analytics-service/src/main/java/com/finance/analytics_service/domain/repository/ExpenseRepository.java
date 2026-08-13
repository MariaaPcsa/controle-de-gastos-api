package com.finance.analytics_service.domain.repository;


import com.finance.analytics_service.domain.model.ExpenseSummary;

import java.util.UUID;

public interface ExpenseRepository {

    ExpenseSummary getSummaryByUser(UUID userId);
}
