package com.finance.transaction_service.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finance.transaction_service.domain.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public KafkaTransactionProducer(KafkaTemplate<String, String> kafkaTemplate,
                                    ObjectMapper objectMapper,
                                    @Value("${kafka.topic.transaction-created:transaction.created}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;

        // Registrar módulo para serializar LocalDateTime
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void publishTransactionCreated(Transaction tx) throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("transactionId", tx.getId().toString());
        payload.put("userId", tx.getUserId());
        payload.put("amount", tx.getAmount());
        if (tx.getOriginalAmount() != null) {
            payload.put("originalAmount", tx.getOriginalAmount());
        }
        payload.put("currency", tx.getCurrency());
        payload.put("type", tx.getType().name());
        payload.put("category", tx.getCategory());
        payload.put("description", tx.getDescription());
        payload.put("createdAt", tx.getCreatedAt());

        String message = objectMapper.writeValueAsString(payload);
        kafkaTemplate.send(topic, tx.getId().toString(), message);
    }
}
