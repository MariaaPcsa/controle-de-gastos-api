package com.finance.analytics_service.domain.service;

import java.math.BigDecimal;

public interface ExchangeRateService {
    BigDecimal convertToBrl(BigDecimal amount, String currency);
}
