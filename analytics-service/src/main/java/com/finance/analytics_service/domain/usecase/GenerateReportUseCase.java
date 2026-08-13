package com.finance.analytics_service.domain.usecase;

import java.util.UUID;

public interface GenerateReportUseCase {
    byte[] generateExcel(UUID userId);
    byte[] generatePdf(UUID userId);
}
