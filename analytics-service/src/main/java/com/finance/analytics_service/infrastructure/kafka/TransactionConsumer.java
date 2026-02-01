package com.finance.analytics_service.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.analytics_service.application.service.AnalysisApplicationService;
import com.finance.analytics_service.infrastructure.kafka.dto.TransactionEventDTO;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TransactionConsumer {

    private final AnalysisApplicationService useCase;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public TransactionConsumer(AnalysisApplicationService useCase) {
        this.useCase = useCase;
    }

    @KafkaListener(topics = "transactions", groupId = "analysis-service")
    public void consume(String payload) {
        try {
            TransactionEventDTO event = mapper.readValue(payload, TransactionEventDTO.class);

            Set<ConstraintViolation<TransactionEventDTO>> violations = validator.validate(event);
            if (!violations.isEmpty()) {
                throw new RuntimeException("Evento inválido: " + violations);
            }

            ExpenseEntity expense = new ExpenseEntity();
            expense.setUserId(event.userId());
            expense.setDescription(event.description());
            expense.setCategory(event.category());
            expense.setAmount(event.amount());
            expense.setDate(event.createdAt().toLocalDate());

            useCase.process(expense);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao consumir Kafka", e);
        }
    }
}
