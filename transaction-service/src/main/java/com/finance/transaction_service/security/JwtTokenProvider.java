package com.finance.transaction_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 🔥 VALIDAR TOKEN
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            parseClaims(token);
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("Token expirado");
        } catch (UnsupportedJwtException e) {
            log.warn("Token não suportado");
        } catch (MalformedJwtException e) {
            log.warn("Token malformado");
        } catch (SecurityException e) {
            log.warn("Assinatura inválida");
        } catch (Exception e) {
            log.error("Erro inesperado ao validar token", e);
        }

        return false;
    }

    // 🔥 EXTRAIR TUDO DE UMA VEZ
    public Claims getClaimsSafe(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            return parseClaims(token);
        } catch (Exception e) {
            log.error("Erro ao extrair claims", e);
            return null;
        }
    }

    public String getUsername(Claims claims) {
        return claims != null ? claims.getSubject() : null;
    }

    public String getRole(Claims claims) {
        if (claims == null)
            return null;

        String role = claims.get("role", String.class);

        if (role == null || role.isBlank()) {
            throw new RuntimeException("Role ausente no token");
        }

        return role;
    }

    public Long getId(Claims claims) {
        if (claims == null)
            return null;

        Object id = claims.get("id");

        if (id instanceof Integer)
            return ((Integer) id).longValue();
        if (id instanceof Long)
            return (Long) id;

        return null;
    }

    // 🔥 MÉTODO CENTRAL
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}