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
import java.io.IOException;

@Component
public class UserExcelImporter {

    private final UserApplicationService service;
    private final PasswordEncoder passwordEncoder;

    public UserExcelImporter(UserApplicationService service,
                             PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    public void importUsers(MultipartFile file) {

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // pula header

                String name = row.getCell(0).getStringCellValue().trim();
                String email = row.getCell(1).getStringCellValue().trim();
                String rawPassword = row.getCell(2).getStringCellValue().trim();

                if (name.isBlank() || email.isBlank() || rawPassword.isBlank()) {
                    continue;
                }

                String hashedPassword = passwordEncoder.encode(rawPassword);

                User user = new User(
                        null,
                        name,
                        email,
                        hashedPassword, // ✅ agora criptografada
                        UserType.USER
                );

                service.create(user);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao importar usuários via Excel", e);
        }
    }
}
