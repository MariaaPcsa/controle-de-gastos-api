package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final UserApplicationService userService;

    public ExcelController(UserApplicationService userService) {
        this.userService = userService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Importar usuários via Excel",
            description = "Recebe um arquivo Excel (.xlsx) e importa os usuários no banco. Apenas ADMIN pode acessar.",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuários importados com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro no envio do arquivo")
            }
    )
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file,
                                         @AuthenticationPrincipal User requester) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Nenhum arquivo enviado.");
        }

        List<String> report = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                try {
                    String name = row.getCell(0).getStringCellValue();
                    String email = row.getCell(1).getStringCellValue();
                    String password = row.getCell(2).getStringCellValue();
                    String typeStr = row.getCell(3).getStringCellValue();
                    UserType type = UserType.valueOf(typeStr.toUpperCase());

                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setType(type);

                    // Chama o serviço de criação/atualização
                    userService.createOrUpdate(user, requester);

                    report.add("Sucesso: " + name);

                } catch (Exception e) {
                    report.add("Erro na linha " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(report);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}
