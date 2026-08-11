package com.finance.transaction_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do JwtTokenProvider")
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private String validToken;

    private final String secret =
            "minha-chave-super-secreta-de-32-bytes-ou-mais";

    @BeforeEach
    void setup() {

        tokenProvider = new JwtTokenProvider(secret);

        validToken = Jwts.builder()
                .setSubject("teste@email.com")
                .claim("id", 1L)
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 3600000)
                )
                .signWith(
                        Keys.hmacShaKeyFor(
                                secret.getBytes(StandardCharsets.UTF_8)
                        ),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    @Test
    @DisplayName("Deve validar token válido")
    void testValidateValidToken() {

        assertTrue(tokenProvider.validateToken(validToken));
    }

    @Test
    @DisplayName("Deve extrair username")
    void testGetUsernameFromValidToken() {

        Claims claims = tokenProvider.getClaimsSafe(validToken);

        String username = tokenProvider.getUsername(claims);

        assertEquals("teste@email.com", username);
    }

    @Test
    @DisplayName("Deve extrair role")
    void testGetRoleFromValidToken() {

        Claims claims = tokenProvider.getClaimsSafe(validToken);

        String role = tokenProvider.getRole(claims);

        assertEquals("USER", role);
    }

    @Test
    @DisplayName("Deve extrair id")
    void testGetIdFromValidToken() {

        Claims claims = tokenProvider.getClaimsSafe(validToken);

        Long id = tokenProvider.getId(claims);

        assertEquals(1L, id);
    }

    @Test
    @DisplayName("Deve retornar null para token inválido")
    void testGetClaimsSafeInvalidToken() {

        Claims claims =
                tokenProvider.getClaimsSafe("token.invalido");

        assertNull(claims);
    }

    @Test
    @DisplayName("Deve rejeitar token expirado")
    void testValidateExpiredToken() {

        String expiredToken = Jwts.builder()
                .setSubject("teste@email.com")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() - 1000)
                )
                .signWith(
                        Keys.hmacShaKeyFor(
                                secret.getBytes(StandardCharsets.UTF_8)
                        ),
                        SignatureAlgorithm.HS256
                )
                .compact();

        assertFalse(tokenProvider.validateToken(expiredToken));
    }

    @Test
    @DisplayName("Deve rejeitar assinatura inválida")
    void testValidateTokenWithInvalidSignature() {

        String otherSecret =
                "outra-chave-super-secreta-de-32-bytes";

        String invalidToken = Jwts.builder()
                .setSubject("teste@email.com")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 3600000)
                )
                .signWith(
                        Keys.hmacShaKeyFor(
                                otherSecret.getBytes(StandardCharsets.UTF_8)
                        ),
                        SignatureAlgorithm.HS256
                )
                .compact();

        assertFalse(tokenProvider.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Deve rejeitar token vazio")
    void testValidateEmptyToken() {

        assertFalse(tokenProvider.validateToken(""));
    }

    @Test
    @DisplayName("Deve rejeitar token null")
    void testValidateNullToken() {

        assertFalse(tokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("Deve rejeitar token malformado")
    void testValidateMalformedToken() {

        assertFalse(
                tokenProvider.validateToken(
                        "not.a.valid.token"
                )
        );
    }
}