package com.finance.transaction_service.presentation.controller;

import com.finance.transaction_service.application.service.TransactionApplicationService;
import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.presentation.dto.*;
import com.finance.transaction_service.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @ApiResponse(responseCode = "400", description = "Dados inválidos")

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(
            @Valid @RequestBody TransactionRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails user) {

        Transaction transaction = service.create(
                user.getId(),
                dto.getDescription(),
                dto.getAmount(),
                dto.getCurrency(),
                dto.getCategory(),
                dto.getType()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    // ================= LIST =================
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar transações com filtros e paginação")
    @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso")
    @GetMapping
    public ResponseEntity<PagedResponseDTO<TransactionResponseDTO>> list(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Categoria para filtro (opcional)")
            @RequestParam(required = false) String category,
            @Parameter(description = "Tipo de transação - INCOME ou EXPENSE (opcional)")
            @RequestParam(required = false) String type,
            @Parameter(description = "Data inicial do período (formato dd-MM-yyyy: 01-01-2024)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data final do período (formato dd-MM-yyyy: 31-12-2024)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Número da página (0-indexed, padrão: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Tamanho da página (padrão: 10)")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "Campo para ordenação (padrão: createdAt)")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direção de ordenação - ASC ou DESC (padrão: DESC)")
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        // Converter String para TransactionType se fornecido
        TransactionType transactionType = null;
        if (type != null && !type.isBlank()) {
            try {
                transactionType = TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tipo de transação inválido: " + type);
            }
        }

        FilterTransactionDTO filter = new FilterTransactionDTO(
                category,
                transactionType,
                startDate,
                endDate,
                page,
                pageSize,
                sortBy,
                sortDirection
        );

        List<TransactionResponseDTO> result = service.list(user.getId(), filter)
                .stream()
                .map(this::toResponse)
                .toList();

        Long totalElements = (long) result.size();
        PagedResponseDTO<TransactionResponseDTO> response = PagedResponseDTO.of(
                result,
                page,
                pageSize,
                totalElements
        );

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deletar transação")
    @ApiResponse(responseCode = "204", description = "Transação deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    @ApiResponse(responseCode = "403", description = "Sem permissão para deletar esta transação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da transação a ser deletada")
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Confirmação de deleção (padrão: true)")
            @RequestParam(defaultValue = "true") Boolean confirmDelete) {

        if (!confirmDelete) {
            return ResponseEntity.badRequest().build();
        }

        service.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ================= UPDATE =================
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar transação")
    @ApiResponse(responseCode = "200", description = "Transação atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar esta transação")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @Parameter(description = "ID da transação a ser atualizada")
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransactionDTO dto,
            @AuthenticationPrincipal CustomUserDetails user) {

        Transaction transaction = service.update(
                id,
                dto.getDescription(),
                dto.getAmount(),
                dto.getAmount(),
                dto.getCategory(),
                dto.getType(),
                dto.getCurrency(),
                user.getId()
        );

        return ResponseEntity.ok(toResponse(transaction));
    }
}