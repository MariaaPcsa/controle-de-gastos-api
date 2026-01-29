package com.maria.finance.user.infrastructure.security;

import com.maria.finance.user.application.service.UserApplicationService;
import com.maria.finance.user.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 1000 * 60 * 60; // 1h

    private final UserApplicationService userService;

    public JwtService(UserApplicationService userService) {
        this.userService = userService;
    }

    // Gerar token
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getType().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    // Validar token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extrair email do token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 🔹 Novo método para obter User do header Authorization
    public User getUserFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido ou ausente");
        }

        String token = authHeader.substring(7);
        if (!validateToken(token)) {
            throw new RuntimeException("Token inválido");
        }

        String email = getEmailFromToken(token);
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário do token não encontrado"));
    }
}
