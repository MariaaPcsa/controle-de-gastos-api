package com.finance.analytics_service.infrastructure.kafka.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEventDTO(

        @NotNull
        Long userId,

        @NotBlank
        String description,

        @NotNull
        @Positive
        BigDecimal amount,

        @NotBlank
        String currency,

        @NotBlank
        String category,

        @NotBlank
        String type,

        @NotNull
        LocalDateTime createdAt

) {}

