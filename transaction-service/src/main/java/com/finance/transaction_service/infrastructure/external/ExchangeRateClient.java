package com.finance.transaction_service.infrastructure.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

@Component
public class ExchangeRateClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal convert(BigDecimal value, String from, String to) {
        try {
            String url = String.format(
                    "https://brasilapi.com.br/api/cambio/v1/converter?valor=%s&moedaOrigem=%s&moedaDestino=%s",
                    value, from, to
            );

            ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
            if (response == null || response.getValorConvertido() == null) {
                throw new RuntimeException("Erro ao converter moeda");
            }

            return response.getValorConvertido();

        } catch (RestClientException e) {
            throw new RuntimeException("Erro ao acessar Brasil API: " + e.getMessage(), e);
        }
    }

    public static class ExchangeRateResponse {
        private BigDecimal valorConvertido;
        public BigDecimal getValorConvertido() { return valorConvertido; }
        public void setValorConvertido(BigDecimal valorConvertido) { this.valorConvertido = valorConvertido; }
    }
}
