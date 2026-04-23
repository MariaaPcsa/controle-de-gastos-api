package com.finance.analytics_service.infrastructure;

import com.finance.analytics_service.infrastructure.persistence.repository.ExpenseRepositoryJpa;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfReportGenerator {

    private final ExpenseRepositoryJpa repository;

    public PdfReportGenerator(ExpenseRepositoryJpa repository) {
        this.repository = repository;
    }

    public byte[] generate(Long userId) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<?> expenses = repository.findByUserId(userId);

            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, out);

            document.open();

            // =========================
            // 📌 CABEÇALHO
            // =========================
            Paragraph header = new Paragraph(
                    "FINANCE ANALYTICS - RELATÓRIO FINANCEIRO",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)
            );
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(new Paragraph(" "));

            // =========================
            // 👤 USUÁRIO
            // =========================
            document.add(new Paragraph("Usuário ID: " + userId));

            document.add(new Paragraph(" "));

            // =========================
            // 📊 RESUMO DINÂMICO
            // =========================
            double total = expenses.stream()
                    .mapToDouble(e -> ((Number) getField(e, "amount")).doubleValue())
                    .sum();

            document.add(new Paragraph("RESUMO FINANCEIRO"));
            document.add(new Paragraph("Total gasto: R$ " + total));
            document.add(new Paragraph("Quantidade de despesas: " + expenses.size()));

            document.add(new Paragraph(" "));

            // =========================
            // 📋 TABELA DINÂMICA
            // =========================
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            addCell(table, "Data");
            addCell(table, "Descrição");
            addCell(table, "Valor");

            for (Object e : expenses) {

                addCell(table, String.valueOf(getField(e, "date")));
                addCell(table, String.valueOf(getField(e, "description")));
                addCell(table, "R$ " + getField(e, "amount"));
            }

            document.add(table);

            // =========================
            // 📌 RODAPÉ
            // =========================
            Paragraph footer = new Paragraph(
                    "Finance Analytics | Página " + writer.getPageNumber(),
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private void addCell(PdfPTable table, String text) {
        table.addCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    // ⚠️ helper genérico (melhor depois trocar por DTO)
    private Object getField(Object obj, String field) {
        try {
            return obj.getClass()
                    .getMethod("get" + capitalize(field))
                    .invoke(obj);
        } catch (Exception e) {
            return "";
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}