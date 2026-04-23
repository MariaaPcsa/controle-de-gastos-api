package com.finance.transaction_service.infrastructure.excel;

import com.finance.transaction_service.application.service.TransactionApplicationService;
import com.finance.transaction_service.domain.model.Transaction;
import com.finance.transaction_service.domain.model.TransactionType;
import com.finance.transaction_service.presentation.dto.ImportErrorDTO;
import com.finance.transaction_service.presentation.dto.ImportResultDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Component
public class TransactionExcelImporter {

    private final TransactionApplicationService service;

    public TransactionExcelImporter(TransactionApplicationService service) {
        this.service = service;
    }

    public ImportResultDTO importFile(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowIndex = 0;
            DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            // skip header
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                rowIndex++;
                Row row = rows.next();
                result.setTotalRows(result.getTotalRows() + 1);

                try {
                    String userIdStr = getCellString(row, 0);
                    String occurredAtStr = getCellString(row, 1);
                    String typeStr = getCellString(row, 2);
                    String amountStr = getCellString(row, 3);
                    String currency = getCellString(row, 4);
                    String category = getCellString(row, 5);
                    String description = getCellString(row, 6);

                    if (userIdStr == null || userIdStr.isBlank()) throw new IllegalArgumentException("userId ausente");
                    Long userId = Long.parseLong(userIdStr.replaceAll("[^0-9]",""));

                    LocalDateTime occurredAt = occurredAtStr == null || occurredAtStr.isBlank()
                            ? LocalDateTime.now()
                            : LocalDateTime.parse(occurredAtStr, dtf);

                    TransactionType type = TransactionType.valueOf(typeStr.toUpperCase());
                    BigDecimal amount = new BigDecimal(amountStr);

                    // Convert currency if required and create transaction via service
                    Transaction tx = service.create(userId, description, amount, currency, category, type);
                    result.setProcessed(result.getProcessed() + 1);
                    result.setSuccess(result.getSuccess() + 1);

                } catch (Exception e) {
                    result.setProcessed(result.getProcessed() + 1);
                    result.setFailed(result.getFailed() + 1);
                    result.addError(new ImportErrorDTO(rowIndex + 1, e.getMessage()));
                }
            }

        } catch (Exception e) {
            result.addError(new ImportErrorDTO(0, "Falha ao processar arquivo: " + e.getMessage()));
        }

        return result;
    }

    private String getCellString(Row row, int index) {
        Cell c = row.getCell(index);
        if (c == null) return null;
        if (c.getCellType() == CellType.STRING) return c.getStringCellValue();
        if (c.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(c)) return c.getLocalDateTimeCellValue().toString();
            return String.valueOf(c.getNumericCellValue());
        }
        if (c.getCellType() == CellType.FORMULA) return c.getStringCellValue();
        return c.toString();
    }
}

