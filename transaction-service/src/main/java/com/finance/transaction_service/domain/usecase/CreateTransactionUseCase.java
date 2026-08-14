package com.finance.transaction_service.domain.usecase;

import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.repository.TransactionRepository;
import com.finance.transaction_service.infrastructure.kafka.KafkaTransactionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTransactionUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(CreateTransactionUseCase.class);

    private final TransactionRepository repository;
    private final KafkaTransactionProducer producer;

    public CreateTransactionUseCase(
            TransactionRepository repository,
            KafkaTransactionProducer producer) {

        this.repository = repository;
        this.producer = producer;
    }

    // =========================================================
    // CREATE
    // =========================================================

    public Transaction execute(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transação não pode ser nula"
            );
        }

        if (transaction.getUserId() == null) {
            throw new IllegalArgumentException(
                    "Usuário da transação é obrigatório"
            );
        }

        // =====================================================
        // PERSISTÊNCIA
        // =====================================================

        Transaction saved = repository.save(transaction);

        // =====================================================
        // EVENTO KAFKA
        // =====================================================

        try {

            producer.publishTransactionCreated(saved);

        } catch (Exception e) {

            /*
             * A falha do Kafka não deve desfazer
             * a criação da transação.
             */
            log.error(
                    "Falha ao publicar evento de criação da transação {}",
                    saved.getId(),
                    e
            );
        }

        return saved;
    }
}