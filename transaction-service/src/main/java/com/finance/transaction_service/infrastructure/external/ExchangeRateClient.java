package com.finance.transaction_service.infrastructure.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ExchangeRateClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal convert(BigDecimal value, String from, String to) {
        String url = "https://brasilapi.com.br/api/cambio/v1/converter"
                + "?valor=" + value + "&moedaOrigem=" + from + "&moedaDestino=" + to;

        Map response = restTemplate.getForObject(url, Map.class);
        return new BigDecimal(response.get("valorConvertido").toString());
    }
}

