package com.finance.analytics_service.infrastructure.kafka.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionEventDTO(

        @NotNull
        UUID userId,

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

