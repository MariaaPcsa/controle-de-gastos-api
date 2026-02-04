package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.infrastructure.security.JwtService;
import com.maria.finance.user.presentation.dto.JwtResponseDTO;
import com.maria.finance.user.presentation.dto.LoginDTO;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserApplicationService service;
    private final JwtService jwt;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserApplicationService service, JwtService jwt, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.jwt = jwt;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO dto) {
        String hashedPassword = passwordEncoder.encode(dto.password());

        User user = new User(
                null,
                dto.name(),
                dto.email(),
                hashedPassword,
                dto.type()
        );

        User created = service.create(user);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(created));
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody LoginDTO dto) {
        User user = service.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwt.generateToken(user);
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }

    // ✅ UPDATE (com JWT no header)
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @RequestBody UserRequestDTO dto,
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);

        String hashedPassword = passwordEncoder.encode(dto.password());

        User updatedUser = service.update(
                id,
                new User(id, dto.name(), dto.email(), hashedPassword, dto.type()),
                requester
        );

        return ResponseEntity.ok(UserResponseDTO.fromDomain(updatedUser));
    }

    // ✅ DELETE (com JWT no header)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);
        service.delete(id, requester);
        return ResponseEntity.noContent().build();
    }

    // ✅ UPLOAD (opcional)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        return ResponseEntity.ok("Arquivo recebido: " + filename);
    }
}
