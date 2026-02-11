package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.infrastructure.security.JwtService;
import com.maria.finance.user.presentation.dto.AuthRequestDTO;
import com.maria.finance.user.presentation.dto.AuthResponseDTO;
import com.maria.finance.user.presentation.dto.JwtResponseDTO;
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

    // ✅ REGISTER → SEMPRE USER
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO dto) {

        String hashedPassword = passwordEncoder.encode(dto.password());

        User user = new User(
                null,
                dto.name(),
                dto.email(),
                hashedPassword,
                UserType.USER
        );

        User created = service.create(user);
        return ResponseEntity.ok(UserResponseDTO.fromDomain(created));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody AuthRequestDTO request) {

        User user = service.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        // 🔒 BLOQUEIA USUÁRIO INATIVO
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Usuário desativado. Acesso bloqueado.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String token = jwt.generateToken(user);

        return ResponseEntity.ok(new JwtResponseDTO(token));
    }


}
