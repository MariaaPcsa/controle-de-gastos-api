package com.finance.transaction_service.presentation.controller;

import com.finance.transaction_service.application.service.TransactionApplicationService;
import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.presentation.dto.TransactionRequestDTO;
import com.finance.transaction_service.presentation.dto.TransactionResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transações", description = "Endpoints para gerenciamento de transações financeiras")
public class TransactionController {

    private final TransactionApplicationService service;

    public TransactionController(TransactionApplicationService service) {
        this.service = service;
    }

    private TransactionResponseDTO toResponse(Transaction t) {
        return TransactionResponseDTO.fromDomain(t);
    }

    // ================= CREATE =================
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar transação")
    @ApiResponse(responseCode = "201", description = "Transação criada com sucesso")
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(
            @Valid @RequestBody TransactionRequestDTO dto,
            @RequestHeader("X-User-Id") Long userId) {

        Transaction transaction = service.create(
                userId,
                dto.getDescription(),
                dto.getAmount(),
                dto.getCurrency(),
                dto.getCategory(),
                dto.getType()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    // ================= LIST =================
    @Operation(summary = "Listar transações")
    @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> list(
            @RequestHeader("X-User-Id") Long userId) {

        List<TransactionResponseDTO> result = service.list(userId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(result);
    }

    // ================= DELETE =================
    @Operation(summary = "Deletar transação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ================= UPDATE =================
    @Operation(summary = "Atualizar transação")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequestDTO dto,
            @RequestHeader("X-User-Id") Long userId) {

        Transaction transaction = service.update(
                id,
                dto.getDescription(),
                dto.getAmount(),
                dto.getAmount(), // originalAmount (pode ser ajustado se necessário)
                dto.getCategory(),
                dto.getType(),
                dto.getCurrency()
        );

        return ResponseEntity.ok(toResponse(transaction));
    }
}