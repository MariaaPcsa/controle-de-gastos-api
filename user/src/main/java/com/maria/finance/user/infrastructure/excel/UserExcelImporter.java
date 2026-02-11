package com.maria.finance.user.infrastructure.excel;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public class UserExcelImporter {

    private final UserApplicationService service;
    private final PasswordEncoder passwordEncoder;

    public UserExcelImporter(UserApplicationService service,
                             PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    public UserImportResult importUsers(MultipartFile file) {

        UserImportResult result = new UserImportResult();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue; // header

                result.incrementTotal();

                try {
                    String name = getCellValue(row.getCell(0));
                    String email = getCellValue(row.getCell(1));
                    String rawPassword = getCellValue(row.getCell(2));

                    if (name.isBlank() || email.isBlank() || rawPassword.isBlank()) {
                        result.incrementInvalid();
                        result.addError("Linha " + (row.getRowNum() + 1) + ": dados obrigatórios em branco");
                        continue;
                    }

                    // 🔒 não deixa email duplicado
                    if (service.findByEmail(email).isPresent()) {
                        result.incrementDuplicated();
                        result.addError("Linha " + (row.getRowNum() + 1) + ": email já cadastrado -> " + email);
                        continue;
                    }

                    String hashedPassword = passwordEncoder.encode(rawPassword);

                    User user = new User(
                            null,
                            name,
                            email,
                            hashedPassword,
                            UserType.USER
                    );

                    service.create(user);
                    result.incrementSuccess();

                } catch (Exception e) {
                    result.incrementInvalid();
                    result.addError("Linha " + (row.getRowNum() + 1) + ": erro ao importar -> " + e.getMessage());
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar arquivo Excel", e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }
}
