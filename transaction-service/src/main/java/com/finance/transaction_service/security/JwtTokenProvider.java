package com.finance.transaction_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("Token inválido: " + e.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            System.err.println("Erro ao extrair username do token: " + e.getMessage());
            return null;
        }
    }

    public String getRole(String token) {
        try {
            String role = getClaims(token).get("role", String.class);
            if (role == null || role.isEmpty()) {
                System.err.println("Role não encontrada no token. Usando padrão: USER");
                return "USER";
            }
            return role;
        } catch (Exception e) {
            System.err.println("Erro ao extrair role do token: " + e.getMessage());
            return "USER"; // Role padrão
        }
    }

    public Long getId(String token) {
        try {
            Integer id = getClaims(token).get("id", Integer.class);
            return id != null ? id.longValue() : null;
        } catch (Exception e) {
            System.err.println("Erro ao extrair id do token: " + e.getMessage());
            return null;
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.err.println("Erro ao fazer parse do token: " + e.getMessage());
            throw e;
        }
    }
}