package com.maria.finance.user.presentation.controller;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.infrastructure.security.JwtService;
import com.maria.finance.user.presentation.dto.JwtResponseDTO;
import com.maria.finance.user.presentation.dto.LoginDTO;
import com.maria.finance.user.presentation.dto.UserRequestDTO;
import com.maria.finance.user.presentation.dto.UserResponseDTO;
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


    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO dto) {
        // Criptografa a senha antes de salvar
        String hashedPassword = passwordEncoder.encode(dto.password());
        User user = new User(null, dto.name(), dto.email(), hashedPassword, dto.type());
        return UserResponseDTO.fromDomain(service.create(user));
    }


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

    @PutMapping("/{id}")
    public UserResponseDTO update(
            @PathVariable Long id,
            @RequestBody UserRequestDTO dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);
        String hashedPassword = passwordEncoder.encode(dto.password());
        User updatedUser = service.update(
                id,
                new User(id, dto.name(), dto.email(), hashedPassword, dto.type()),
                requester
        );
        return UserResponseDTO.fromDomain(updatedUser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User requester = jwt.getUserFromHeader(authHeader);
        service.delete(id, requester);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        return ResponseEntity.ok("Arquivo recebido: " + filename);
    }

}
