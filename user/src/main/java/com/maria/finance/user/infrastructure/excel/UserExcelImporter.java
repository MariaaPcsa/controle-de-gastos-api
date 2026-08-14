package com.maria.finance.user.infrastructure.excel;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public class UserExcelImporter {

    private final UserApplicationService service;

    public UserExcelImporter(UserApplicationService service) {
        this.service = service;
    }

    public UserImportResult importUsers(MultipartFile file) {

        UserImportResult result = new UserImportResult();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue; // header

                int linha = row.getRowNum() + 1;
                result.incrementTotal();

                try {
                    String name = getCellValue(row.getCell(0));
                    String email = getCellValue(row.getCell(1));
                    String rawPassword = getCellValue(row.getCell(2));

                    // 🔴 CAMPOS OBRIGATÓRIOS
                    if (name.isBlank() || email.isBlank() || rawPassword.isBlank()) {
                        addError(result, linha, "INVALIDO", "Campos obrigatórios em branco", email);
                        continue;
                    }

                    // 🔴 EMAIL INVÁLIDO
                    if (!isValidEmail(email)) {
                        addError(result, linha, "EMAIL_INVALIDO", "Formato de email inválido", email);
                        continue;
                    }

                    // 🔴 SENHA FRACA
                    if (rawPassword.length() < 6) {
                        addError(result, linha, "SENHA_FRACA", "Senha deve ter no mínimo 6 caracteres", email);
                        continue;
                    }

                    // 🔴 DUPLICADO
                    if (service.findByEmail(email).isPresent()) {
                        result.incrementDuplicated();
                        result.addError(new UserImportError(
                                linha,
                                "DUPLICADO",
                                "Email já cadastrado",
                                email
                        ));
                        continue;
                    }

                    // ✅ CRIA USUÁRIO
                    User user = new User(
                            null,
                            name,
                            email,
                            rawPassword, // 🔐 criptografia deve ocorrer no service
                            UserType.USER
                    );

                    service.create(user);
                    result.incrementSuccess();

                } catch (Exception e) {
                    addError(result, linha, "ERRO", e.getMessage(), null);
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar arquivo Excel", e);
        }
    }

    // 🔧 MÉTODO AUXILIAR DE ERRO
    private void addError(UserImportResult result, int linha, String tipo, String mensagem, String email) {
        result.incrementInvalid();
        result.addError(new UserImportError(linha, tipo, mensagem, email));
    }

    // 🔧 VALIDAÇÃO DE EMAIL
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    // 🔧 LEITURA DE CÉLULA
    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}