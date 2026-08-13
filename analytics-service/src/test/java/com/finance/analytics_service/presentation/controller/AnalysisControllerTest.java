package com.finance.analytics_service.presentation.controller;

import com.finance.analytics_service.AnalysisApplicationService;
import com.finance.analytics_service.domain.model.ExpenseSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalysisApplicationService service;

    @Test
    void shouldReturnSummaryForUuidUser() throws Exception {
        UUID userId = UUID.fromString("4967ead6-10b1-450d-af05-7605a1ced37d");
        ExpenseSummary summary = new ExpenseSummary(
                new BigDecimal("580.00"),
                new BigDecimal("1280.00"),
                Map.of("ALIMENTACAO", new BigDecimal("580.00"))
        );

        when(service.getSummary(userId)).thenReturn(summary);

        mockMvc.perform(get("/api/analysis/summary/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMes").value(580.00))
                .andExpect(jsonPath("$.totalAno").value(1280.00))
                .andExpect(jsonPath("$.totalPorCategoria.ALIMENTACAO").value(580.00));

        verify(service).getSummary(userId);
    }

    @Test
    void shouldReturnExcelForUuidUser() throws Exception {
        UUID userId = UUID.fromString("4967ead6-10b1-450d-af05-7605a1ced37d");
        byte[] file = "excel-content".getBytes();

        when(service.generateExcel(userId)).thenReturn(file);

        mockMvc.perform(get("/api/analysis/report/excel/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=relatorio.xlsx"))
                .andExpect(content().bytes(file));

        verify(service).generateExcel(userId);
    }

    @Test
    void shouldReturnPdfForUuidUser() throws Exception {
        UUID userId = UUID.fromString("4967ead6-10b1-450d-af05-7605a1ced37d");
        byte[] file = "pdf-content".getBytes();

        when(service.generatePdf(userId)).thenReturn(file);

        mockMvc.perform(get("/api/analysis/report/pdf/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=relatorio.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(file));

        verify(service).generatePdf(userId);
    }

    @Test
    void shouldReturnBadRequestWhenUserIdIsNotUuid() throws Exception {
        mockMvc.perform(get("/api/analysis/summary/{userId}", "123"))
                .andExpect(status().isBadRequest());
    }
}
