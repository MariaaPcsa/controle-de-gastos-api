package com.finance.analytics_service.presentation.controller;

import com.finance.analytics_service.application.service.AnalysisApplicationService;
import com.finance.analytics_service.domain.model.ExpenseSummary;
import com.finance.analytics_service.infrastructure.report.ExcelReportGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisApplicationService service;

    public AnalysisController(AnalysisApplicationService service) {
        this.service = service;
    }

    @GetMapping("/summary/{userId}")
    public List<ExpenseSummary> summary(@PathVariable Long userId) {
        return service.report().byCategory(userId);
    }

    @GetMapping("/report/excel/{userId}")
    public ResponseEntity<byte[]> excel(@PathVariable Long userId) {

        var data = service.report().byCategory(userId);
        byte[] file = ExcelReportGenerator.generate(data);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.xlsx")
                .body(file);
    }
}

