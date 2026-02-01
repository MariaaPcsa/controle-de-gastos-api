package com.finance.analytics_service.infrastructure.external;

import com.finance.analytics_service.domain.service.ExchangeRateService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Profile("prod")
public class BrasilApiExchangeRateClient implements ExchangeRateService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public BigDecimal convertToBrl(BigDecimal amount, String currency) {
        if ("BRL".equalsIgnoreCase(currency)) {
            return amount;
        }

        String url = "https://brasilapi.com.br/api/cambio/v1/moedas/" + currency + "-BRL";
        Map response = restTemplate.getForObject(url, Map.class);

        Map data = (Map) response.get("data");
        BigDecimal bid = new BigDecimal(data.get("bid").toString());

        return amount.multiply(bid);
    }
}
