package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.infrastructure.excel.UserExcelImporter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UploadController {

    private final UserExcelImporter importer;

    public UploadController(UserExcelImporter importer) {
        this.importer = importer;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {

        importer.importUsers(file); // ✅ uso correto

        return ResponseEntity.ok().build();
    }
}


