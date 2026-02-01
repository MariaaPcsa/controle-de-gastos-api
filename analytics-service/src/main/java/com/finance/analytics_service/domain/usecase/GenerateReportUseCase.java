package com.finance.analytics_service.domain.usecase;



public interface GenerateReportUseCase {
    byte[] generateExcel(Long userId);
    byte[] generatePdf(Long userId);
}
