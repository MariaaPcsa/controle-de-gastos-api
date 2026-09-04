package com.finance.transaction_service.presentation.controller;

import com.finance.transaction_service.application.service.TransactionApplicationService;
import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.infrastructure.excel.TransactionExcelImporter;
import com.finance.transaction_service.presentation.dto.*;
import com.finance.transaction_service.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@Tag(
        name = "Transações",
        description = "Endpoints para gerenciamento de transações financeiras"
)
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionApplicationService service;
    private final TransactionExcelImporter excelImporter;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    // Constructor usado pelos testes
    public TransactionController(
            TransactionApplicationService service) {

        this(service, Optional.empty());
    }

    // Constructor usado pelo Spring
    @Autowired
    public TransactionController(
            TransactionApplicationService service,
            Optional<TransactionExcelImporter> excelImporterOpt) {

        this.service = service;
        this.excelImporter = excelImporterOpt.orElse(null);
    }

    // =========================================================
    // RESPONSE MAPPER
    // =========================================================

    private TransactionResponseDTO toResponse(
            Transaction transaction) {

        return TransactionResponseDTO.fromDomain(transaction);
    }

    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    @Operation(summary = "Criar minha transação")
    @ApiResponse(
            responseCode = "201",
            description = "Transação criada com sucesso"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado"
    )
    public ResponseEntity<TransactionResponseDTO> create(

            @Valid
            @RequestBody
            TransactionRequestDTO dto,

            @AuthenticationPrincipal
            CustomUserDetails user) {

        if (user == null || user.getId() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Transaction transaction = service.create(
                user.getId(),
                dto.getDescription(),
                dto.getAmount(),
                dto.getCurrency(),
                dto.getCategory(),
                dto.getType()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(transaction));
    }

    // =========================================================
    // LIST
    // =========================================================

    @GetMapping
    @Operation(summary = "Listar minhas transações")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de transações retornada com sucesso"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado"
    )
    public ResponseEntity<PagedResponseDTO<TransactionResponseDTO>> list(

            @AuthenticationPrincipal
            CustomUserDetails user,

            @Parameter(description = "Categoria para filtro")
            @RequestParam(required = false)
            String category,

            @Parameter(description = "DEPOSIT, WITHDRAW, TRANSFER ou PURCHASE")
            @RequestParam(required = false)
            String type,

            @Parameter(description = "Data inicial")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @Parameter(description = "Data final")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(defaultValue = "0")
            Integer page,

            @RequestParam(defaultValue = "10")
            Integer pageSize,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String sortDirection) {

        if (user == null || user.getId() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        TransactionType transactionType = null;

        if (type != null && !type.isBlank()) {
            try {
                transactionType =
                        TransactionType.valueOf(
                                type.toUpperCase()
                        );

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Tipo de transação inválido: " + type
                );
            }
        }

        FilterTransactionDTO filter =
                new FilterTransactionDTO(
                        category,
                        transactionType,
                        startDate,
                        endDate,
                        page,
                        pageSize,
                        sortBy,
                        sortDirection
                );

        List<TransactionResponseDTO> result =
                service.list(
                                user.getId(),
                                filter
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        Long totalElements =
                (long) result.size();

        PagedResponseDTO<TransactionResponseDTO> response =
                PagedResponseDTO.of(
                        result,
                        page,
                        pageSize,
                        totalElements
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    @Operation(summary = "Consultar minha transação por ID")
    @ApiResponse(
            responseCode = "200",
            description = "Transação retornada com sucesso"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado"
    )
    @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para consultar"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Transação não encontrada"
    )
    public ResponseEntity<TransactionResponseDTO> getById(

            @Parameter(description = "ID da transação")
            @PathVariable UUID id,

            @AuthenticationPrincipal
            CustomUserDetails user) {

        if (user == null || user.getId() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Transaction transaction =
                service.getById(id, user.getId());

        return ResponseEntity.ok(
                toResponse(transaction)
        );
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar minha transação")
    @ApiResponse(
            responseCode = "200",
            description = "Transação atualizada com sucesso"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Transação não encontrada"
    )
    @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para atualizar"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado"
    )
    public ResponseEntity<TransactionResponseDTO> update(

            @Parameter(description = "ID da transação")
            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateTransactionDTO dto,

            @AuthenticationPrincipal
            CustomUserDetails user) {

        if (user == null || user.getId() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        /*
         * O UpdateTransactionDTO não possui getOriginalAmount().
         *
         * Portanto:
         *
         * amount         = novo valor informado pelo usuário
         * originalAmount = novo valor informado pelo usuário
         *
         * O ApplicationService será responsável por converter
         * amount para BRL quando necessário.
         */
        Transaction transaction =
                service.update(
                        id,
                        dto.getDescription(),
                        dto.getAmount(),
                        dto.getAmount(),
                        dto.getCategory(),
                        dto.getType(),
                        dto.getCurrency(),
                        user.getId()
                );

        return ResponseEntity.ok(
                toResponse(transaction)
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar minha transação")
    @ApiResponse(
            responseCode = "204",
            description = "Transação deletada com sucesso"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Transação não encontrada"
    )
    @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para deletar"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado"
    )
    public ResponseEntity<Void> delete(

            @Parameter(description = "ID da transação")
            @PathVariable UUID id,

            @AuthenticationPrincipal
            CustomUserDetails user,

            @RequestParam(defaultValue = "true")
            Boolean confirmDelete) {

        if (user == null || user.getId() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        if (!Boolean.TRUE.equals(confirmDelete)) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        service.delete(
                id,
                user.getId()
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    // =========================================================
    // IMPORT EXCEL
    // =========================================================

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Importar minhas transações via planilha XLSX"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Importação processada"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Arquivo inválido"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado"
    )
    public ResponseEntity<ImportResultDTO> upload(

            @RequestPart("file")
            MultipartFile file,

            @AuthenticationPrincipal
            CustomUserDetails user) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        if (user == null || user.getId() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        if (excelImporter == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_IMPLEMENTED)
                    .build();
        }

        ImportResultDTO result =
                excelImporter.importFile(
                        file,
                        user.getId()
                );

        return ResponseEntity.ok(result);
    }
}