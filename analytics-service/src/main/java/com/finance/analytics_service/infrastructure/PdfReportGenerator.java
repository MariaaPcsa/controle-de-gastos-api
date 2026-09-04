package com.finance.analytics_service.infrastructure;

import com.finance.analytics_service.infrastructure.persistence.repository.ExpenseRepositoryJpa;
import com.finance.analytics_service.infrastructure.persistence.entity.ExpenseEntity;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class PdfReportGenerator {

    private final ExpenseRepositoryJpa repository;

    public PdfReportGenerator(ExpenseRepositoryJpa repository) {
        this.repository = repository;
    }

    public byte[] generate(UUID userId) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<ExpenseEntity> expenses = repository.findByUserId(userId)
                    .stream()
                    .sorted(Comparator.comparing(ExpenseEntity::getDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .toList();

            BigDecimal total = expenses.stream()
                    .map(ExpenseEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal average = expenses.isEmpty()
                    ? BigDecimal.ZERO
                    : total.divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);

            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, out);

            document.open();

            // =========================
            // 📌 CABEÇALHO
            // =========================
            Paragraph header = new Paragraph(
                    "FINANCE ANALYTICS - RELATÓRIO FINANCEIRO",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
            );
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(new Paragraph(" "));

            // =========================
            // 👤 USUÁRIO
            // =========================
            Paragraph userLine = new Paragraph(
                    "Usuário ID: " + userId,
                    FontFactory.getFont(FontFactory.HELVETICA, 10)
            );
            userLine.setAlignment(Element.ALIGN_LEFT);
            document.add(userLine);

            document.add(new Paragraph(" "));

            // =========================
            // 📊 RESUMO DINÂMICO
            // =========================
            PdfPTable summaryTable = new PdfPTable(3);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(5);
            summaryTable.setSpacingAfter(10);

            addHeaderCell(summaryTable, "Total Gasto");
            addHeaderCell(summaryTable, "Média");
            addHeaderCell(summaryTable, "Quantidade");

            addValueCell(summaryTable, formatCurrency(total));
            addValueCell(summaryTable, formatCurrency(average));
            addValueCell(summaryTable, String.valueOf(expenses.size()));

            document.add(summaryTable);

            document.add(new Paragraph(" "));

            // =========================
            // 📋 TABELA DINÂMICA
            // =========================
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.0f, 4.5f, 3.0f, 2.5f});
            table.setSpacingBefore(5);

            addHeaderCell(table, "Data");
            addHeaderCell(table, "Descrição");
            addHeaderCell(table, "Categoria");
            addHeaderCell(table, "Valor");

            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (expenses.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("Sem despesas para o usuário informado."));
                emptyCell.setColspan(4);
                emptyCell.setPadding(8f);
                table.addCell(emptyCell);
            } else {
                for (ExpenseEntity e : expenses) {

                    addBodyCell(table, e.getDate() != null ? e.getDate().format(dateFormat) : "-");
                    addBodyCell(table, e.getDescription() != null ? e.getDescription() : "-");
                    addBodyCell(table, e.getCategory() != null ? e.getCategory() : "-");
                    addBodyCell(table, formatCurrency(e.getAmount()));
                }
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

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        cell.setBackgroundColor(new Color(30, 58, 138));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(7f);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private void addValueCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) {
            return "R$ 0,00";
        }

        BigDecimal normalized = value.setScale(2, RoundingMode.HALF_UP);
        return "R$ " + normalized.toPlainString().replace('.', ',');
    }
}