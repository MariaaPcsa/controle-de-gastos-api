package com.maria.finance.user.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreated(UserResponseDTO dto) {
        try {
            String payload = mapper.writeValueAsString(dto);
            kafkaTemplate.send("users", payload);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar evento de usuário", e);
        }
    }
}

