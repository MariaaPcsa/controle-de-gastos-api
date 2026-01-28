//package com.finance.transaction_service.infrastructure.kafka;
//
//@Service
//public class TransactionProducer {
//
//    private final KafkaTemplate<String, TransactionResponseDTO> kafka;
//
//    public TransactionProducer(KafkaTemplate<String, TransactionResponseDTO> kafka) {
//        this.kafka = kafka;
//    }
//
//    public void publish(TransactionResponseDTO dto) {
//        kafka.send("transactions-topic", dto);
//    }
//}
