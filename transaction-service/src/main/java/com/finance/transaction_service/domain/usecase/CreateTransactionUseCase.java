package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import com.finance.transaction_service.infrastructure.kafka.KafkaTransactionProducer;

public class CreateTransactionUseCase {

    private final TransactionRepository repository;
    private final KafkaTransactionProducer producer;

    public CreateTransactionUseCase(TransactionRepository repository, KafkaTransactionProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    // Agora recebe um Transaction pronto, validado e com valor convertido
    public Transaction execute(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transação não pode ser nula");
        }

        // Persistência
        Transaction saved = repository.save(transaction);

        // Publica evento de criação
        try {
            producer.publishTransactionCreated(saved);
        } catch (Exception e) {
            // Log e seguir (não falhar a operação principal)
            System.err.println("Falha ao publicar evento Kafka: " + e.getMessage());
        }

        return saved;
    }
}
