package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.infrastructure.security.JwtService;
import com.maria.finance.user.presentation.dto.LoginDTO;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserApplicationService service;
    private final JwtService jwt;

    public AuthController(UserApplicationService service, JwtService jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO dto) {
        User user = new User(null, dto.name(), dto.email(), dto.password(), dto.type());
        return UserResponseDTO.fromDomain(service.create(user));
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto) {
        // simplificado para o desafio
        return "TOKEN_JWT_AQUI";
    }
}

