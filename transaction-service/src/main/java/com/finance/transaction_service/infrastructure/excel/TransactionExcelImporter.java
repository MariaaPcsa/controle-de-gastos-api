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
import java.util.UUID;

@Component
public class TransactionExcelImporter {

    private final TransactionApplicationService service;

    public TransactionExcelImporter(
            TransactionApplicationService service) {

        this.service = service;
    }

    // =========================================================
    // IMPORT
    // =========================================================

    public ImportResultDTO importFile(
            MultipartFile file,
            UUID userId) {

        ImportResultDTO result = new ImportResultDTO();

        if (file == null || file.isEmpty()) {

            result.addError(
                    new ImportErrorDTO(
                            0,
                            "Arquivo não informado ou vazio"
                    )
            );

            return result;
        }

        if (userId == null) {

            result.addError(
                    new ImportErrorDTO(
                            0,
                            "Usuário autenticado não identificado"
                    )
            );

            return result;
        }

        try (
                InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)
        ) {

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();

            int rowIndex = 0;

            DateTimeFormatter dtf =
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            // =================================================
            // IGNORAR CABEÇALHO
            // =================================================

            if (rows.hasNext()) {
                rows.next();
                rowIndex++;
            }

            // =================================================
            // PROCESSAR LINHAS
            // =================================================

            while (rows.hasNext()) {

                Row row = rows.next();

                rowIndex++;

                result.setTotalRows(
                        result.getTotalRows() + 1
                );

                try {

                    /*
                     * IMPORTANTE:
                     *
                     * O userId NÃO vem mais da planilha.
                     *
                     * Ele vem do JWT autenticado.
                     */

                    String occurredAtStr =
                            getCellString(row, 0);

                    String typeStr =
                            getCellString(row, 1);

                    String amountStr =
                            getCellString(row, 2);

                    String currency =
                            getCellString(row, 3);

                    String category =
                            getCellString(row, 4);

                    String description =
                            getCellString(row, 5);

                    // =========================================
                    // DATA
                    // =========================================

                    LocalDateTime occurredAt =
                            occurredAtStr == null
                                    || occurredAtStr.isBlank()
                                    ? LocalDateTime.now()
                                    : LocalDateTime.parse(
                                    occurredAtStr,
                                    dtf
                            );

                    // =========================================
                    // TIPO
                    // =========================================

                    if (typeStr == null
                            || typeStr.isBlank()) {

                        throw new IllegalArgumentException(
                                "Tipo da transação é obrigatório"
                        );
                    }

                    TransactionType type =
                            TransactionType.valueOf(
                                    typeStr.toUpperCase()
                            );

                    // =========================================
                    // VALOR
                    // =========================================

                    if (amountStr == null
                            || amountStr.isBlank()) {

                        throw new IllegalArgumentException(
                                "Valor da transação é obrigatório"
                        );
                    }

                    BigDecimal amount =
                            new BigDecimal(amountStr);

                    // =========================================
                    // CRIAR TRANSAÇÃO
                    // =========================================

                    Transaction tx =
                            service.create(
                                    userId,
                                    description,
                                    amount,
                                    currency,
                                    category,
                                    type
                            );

                    result.setProcessed(
                            result.getProcessed() + 1
                    );

                    result.setSuccess(
                            result.getSuccess() + 1
                    );

                } catch (Exception e) {

                    result.setProcessed(
                            result.getProcessed() + 1
                    );

                    result.setFailed(
                            result.getFailed() + 1
                    );

                    result.addError(
                            new ImportErrorDTO(
                                    rowIndex,
                                    e.getMessage()
                            )
                    );
                }
            }

        } catch (Exception e) {

            result.addError(
                    new ImportErrorDTO(
                            0,
                            "Falha ao processar arquivo: "
                                    + e.getMessage()
                    )
            );
        }

        return result;
    }

    // =========================================================
    // LER CÉLULA
    // =========================================================

    private String getCellString(
            Row row,
            int index) {

        Cell cell = row.getCell(index);

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.STRING) {

            return cell
                    .getStringCellValue()
                    .trim();
        }

        if (cell.getCellType() == CellType.NUMERIC) {

            if (DateUtil.isCellDateFormatted(cell)) {

                return cell
                        .getLocalDateTimeCellValue()
                        .toString();
            }

            return String.valueOf(
                    cell.getNumericCellValue()
            );
        }

        if (cell.getCellType() == CellType.FORMULA) {

            return cell
                    .getStringCellValue()
                    .trim();
        }

        return cell.toString().trim();
    }
}