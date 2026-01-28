//package com.finance.analytics_service.infrastructure.kafka;
//
//@Service
//public class TransactionConsumer {
//
//    private final AnalysisApplicationService service;
//
//    @KafkaListener(topics = "transactions-topic", groupId = "analysis-group")
//    public void consume(TransactionResponseDTO dto) {
//        service.processTransaction(
//                dto.getUserId(),
//                dto.getCategory(),
//                dto.getType(),
//                dto.getAmount().doubleValue(),
//                dto.getDate().toLocalDate()
//        );
//    }
//}

