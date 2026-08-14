package com.finance.analytics_service.infrastructure.external;

import com.finance.analytics_service.domain.service.ExchangeRateService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("test")
public class MockExchangeRateClient implements ExchangeRateService {

    @Override
    public BigDecimal convertToBrl(BigDecimal amount, String currency) {
        if ("BRL".equalsIgnoreCase(currency)) {
            return amount;
        }
        return amount.multiply(BigDecimal.valueOf(5.00)); // taxa fake
    }
}

