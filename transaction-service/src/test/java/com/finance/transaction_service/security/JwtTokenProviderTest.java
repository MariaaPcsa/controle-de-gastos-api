package com.finance.transaction_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do JwtTokenProvider - Tratamento de Exceções")
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private String validToken;
    private String secret = "minha-chave-super-secreta-de-32-bytes-ou-mais";

    @BeforeEach
    void setup() {
        tokenProvider = new JwtTokenProvider(secret);

        // Gerar um token válido com role
        validToken = Jwts.builder()
                .setSubject("teste@email.com")
                .claim("id", 1L)
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),
                         SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    @DisplayName("Deve validar um token válido com role")
    void testValidateValidToken() {
        assertTrue(tokenProvider.validateToken(validToken));
    }

    @Test
    @DisplayName("Deve extrair username corretamente de um token válido")
    void testGetUsernameFromValidToken() {
        String username = tokenProvider.getUsername(validToken);
        assertEquals("teste@email.com", username);
    }

    @Test
    @DisplayName("Deve extrair role corretamente de um token válido")
    void testGetRoleFromValidToken() {
        String role = tokenProvider.getRole(validToken);
        assertEquals("USER", role);
    }

    @Test
    @DisplayName("Deve retornar role padrão quando role não estiver no token")
    void testGetRoleWhenRoleIsMissingReturnsDefault() {
        // Token sem role
        String tokenWithoutRole = Jwts.builder()
                .setSubject("teste@email.com")
                .claim("id", 1L)
                // Sem role!
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),
                         SignatureAlgorithm.HS256)
                .compact();

        String role = tokenProvider.getRole(tokenWithoutRole);
        assertEquals("USER", role); // Role padrão
    }

    @Test
    @DisplayName("Deve retornar null quando username não puder ser extraído")
    void testGetUsernameWhenTokenIsInvalid() {
        String invalidToken = "invalid.token.here";
        String username = tokenProvider.getUsername(invalidToken);
        assertNull(username);
    }

    @Test
    @DisplayName("Deve retornar role padrão quando há erro ao extrair role")
    void testGetRoleWhenTokenIsInvalidReturnsDefault() {
        String invalidToken = "invalid.token.here";
        String role = tokenProvider.getRole(invalidToken);
        assertEquals("USER", role); // Role padrão
    }

    @Test
    @DisplayName("Deve rejeitar token expirado")
    void testValidateExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("teste@email.com")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expirado
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),
                         SignatureAlgorithm.HS256)
                .compact();

        assertFalse(tokenProvider.validateToken(expiredToken));
    }

    @Test
    @DisplayName("Deve rejeitar token com chave inválida")
    void testValidateTokenWithInvalidSignature() {
        String differentSecret = "chave-super-secreta-diferente-de-32-bytes-ou-mais";
        String tokenWithDifferentSecret = Jwts.builder()
                .setSubject("teste@email.com")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8)),
                         SignatureAlgorithm.HS256)
                .compact();

        assertFalse(tokenProvider.validateToken(tokenWithDifferentSecret));
    }

    @Test
    @DisplayName("Deve rejeitar token null")
    void testValidateNullToken() {
        assertFalse(tokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("Deve rejeitar token vazio")
    void testValidateEmptyToken() {
        assertFalse(tokenProvider.validateToken(""));
    }

    @Test
    @DisplayName("Deve rejeitar token malformado")
    void testValidateMalformedToken() {
        assertFalse(tokenProvider.validateToken("not.a.valid.token.structure"));
    }

    @Test
    @DisplayName("Deve extrair role com diferentes valores (ADMIN, USER)")
    void testGetRoleWithDifferentRoleValues() {
        // Token com role ADMIN
        String adminToken = Jwts.builder()
                .setSubject("admin@email.com")
                .claim("role", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),
                         SignatureAlgorithm.HS256)
                .compact();

        assertEquals("ADMIN", tokenProvider.getRole(adminToken));
    }
}

