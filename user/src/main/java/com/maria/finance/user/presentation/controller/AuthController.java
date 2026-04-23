package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.infrastructure.security.JwtService;
import com.maria.finance.user.presentation.dto.AuthRequestDTO;
import com.maria.finance.user.presentation.dto.JwtResponseDTO;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserApplicationService service;
    private final JwtService jwt;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserApplicationService service,
                          JwtService jwt,
                          PasswordEncoder passwordEncoder) {
        this.service = service;
        this.jwt = jwt;
        this.passwordEncoder = passwordEncoder;
    }

    // 🚀 REGISTRO
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO dto) {

        // validação básica
        if (dto.email() == null || dto.password() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email e senha são obrigatórios"));
        }

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

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {

        // busca usuário
        User user = service.findByEmail(request.getEmail())
                .orElse(null);

        // credenciais inválidas
        if (user == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            return ResponseEntity.status(401)
                    .body(Map.of("error", "Email ou senha inválidos"));
        }

        // usuário inativo
        if (!Boolean.TRUE.equals(user.getActive())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Usuário desativado"));
        }

        // gera token
        String token = jwt.generateToken(user);

        return ResponseEntity.ok(new JwtResponseDTO(token));
    }
}