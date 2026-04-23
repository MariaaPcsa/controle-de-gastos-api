package com.finance.analytics_service.presentation.controller;

import com.finance.analytics_service.AnalysisApplicationService;
import com.finance.analytics_service.domain.model.ExpenseSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@Tag(name = "Analysis", description = "Relatórios e análise de despesas")
public class AnalysisController {

    private final AnalysisApplicationService service;

    public AnalysisController(AnalysisApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Busca resumo de despesas por usuário")
    @GetMapping("/summary/{userId}")
    public ExpenseSummary summary(@PathVariable Long userId) {
        return service.getSummary(userId);
    }

    @Operation(summary = "Gera relatório Excel de despesas")
    @GetMapping("/report/excel/{userId}")
    public ResponseEntity<byte[]> excel(@PathVariable Long userId) {
        byte[] file = service.generateExcel(userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @Operation(summary = "Gera relatório PDF de despesas")
    @GetMapping("/report/pdf/{userId}")
    public ResponseEntity<byte[]> pdf(@PathVariable Long userId) {
        byte[] file = service.generatePdf(userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}