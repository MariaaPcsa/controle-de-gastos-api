//package com.finance.transaction_service.infrastructure.kafka;
//
//import com.finance.transaction_service.presentation.dto.TransactionResponseDTO;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TransactionProducer {
//
//    private final KafkaTemplate<String, TransactionResponseDTO> kafkaTemplate;
//
//    public TransactionProducer(KafkaTemplate<String, TransactionResponseDTO> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void publish(TransactionResponseDTO dto) {
//        kafkaTemplate.send("transactions-topic", dto);
//    }
//}

