package com.finance.analytics_service.application.service;



import com.finance.analytics_service.domain.usecase.GenerateReportUseCase;
import com.finance.analytics_service.domain.usecase.ProcessTransactionUseCase;

import java.time.LocalDate;

public class AnalysisApplicationService {

    private final ProcessTransactionUseCase process;
    private final GenerateReportUseCase report;

    public AnalysisApplicationService(ProcessTransactionUseCase process,
                                      GenerateReportUseCase report) {
        this.process = process;
        this.report = report;
    }

    public void processTransaction(Long userId, String category,
                                   String type, double amount,
                                   LocalDate date) {
        process.execute(userId, category, type, amount, date);
    }

    public GenerateReportUseCase report() {
        return report;
    }
}

