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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do JwtTokenProvider")
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    private String validToken;

    private UUID userId;

    private final String secret =
            "minha-chave-super-secreta-de-32-bytes-ou-mais";

    @BeforeEach
    void setup() {

        tokenProvider =
                new JwtTokenProvider(secret);

        userId =
                UUID.randomUUID();

        validToken =
                Jwts.builder()
                        .setSubject("teste@email.com")

                        // IMPORTANTE:
                        // agora usamos userId como UUID
                        .claim(
                                "userId",
                                userId.toString()
                        )

                        .claim(
                                "role",
                                "USER"
                        )

                        .setIssuedAt(
                                new Date()
                        )

                        .setExpiration(
                                new Date(
                                        System.currentTimeMillis()
                                                + 3600000
                                )
                        )

                        .signWith(
                                Keys.hmacShaKeyFor(
                                        secret.getBytes(
                                                StandardCharsets.UTF_8
                                        )
                                ),
                                SignatureAlgorithm.HS256
                        )

                        .compact();
    }

    // =========================================================
    // VALID TOKEN
    // =========================================================

    @Test
    @DisplayName("Deve validar token válido")
    void testValidateValidToken() {

        assertTrue(
                tokenProvider.validateToken(
                        validToken
                )
        );
    }

    // =========================================================
    // USERNAME
    // =========================================================

    @Test
    @DisplayName("Deve extrair username")
    void testGetUsernameFromValidToken() {

        Claims claims =
                tokenProvider.getClaimsSafe(
                        validToken
                );

        assertNotNull(claims);

        String username =
                tokenProvider.getUsername(
                        claims
                );

        assertEquals(
                "teste@email.com",
                username
        );
    }

    // =========================================================
    // ROLE
    // =========================================================

    @Test
    @DisplayName("Deve extrair role")
    void testGetRoleFromValidToken() {

        Claims claims =
                tokenProvider.getClaimsSafe(
                        validToken
                );

        assertNotNull(claims);

        String role =
                tokenProvider.getRole(
                        claims
                );

        assertEquals(
                "USER",
                role
        );
    }

    // =========================================================
    // USER ID
    // =========================================================

    @Test
    @DisplayName("Deve extrair UUID do usuário")
    void testGetUserIdFromValidToken() {

        Claims claims =
                tokenProvider.getClaimsSafe(
                        validToken
                );

        assertNotNull(claims);

        UUID extractedUserId =
                tokenProvider.getUserId(
                        claims
                );

        assertNotNull(
                extractedUserId
        );

        assertEquals(
                userId,
                extractedUserId
        );
    }

    // =========================================================
    // INVALID TOKEN
    // =========================================================

    @Test
    @DisplayName("Deve retornar null para token inválido")
    void testGetClaimsSafeInvalidToken() {

        Claims claims =
                tokenProvider.getClaimsSafe(
                        "token.invalido"
                );

        assertNull(claims);
    }

    // =========================================================
    // EXPIRED TOKEN
    // =========================================================

    @Test
    @DisplayName("Deve rejeitar token expirado")
    void testValidateExpiredToken() {

        String expiredToken =
                Jwts.builder()

                        .setSubject(
                                "teste@email.com"
                        )

                        .claim(
                                "userId",
                                userId.toString()
                        )

                        .claim(
                                "role",
                                "USER"
                        )

                        .setIssuedAt(
                                new Date(
                                        System.currentTimeMillis()
                                                - 2000
                                )
                        )

                        .setExpiration(
                                new Date(
                                        System.currentTimeMillis()
                                                - 1000
                                )
                        )

                        .signWith(
                                Keys.hmacShaKeyFor(
                                        secret.getBytes(
                                                StandardCharsets.UTF_8
                                        )
                                ),
                                SignatureAlgorithm.HS256
                        )

                        .compact();

        assertFalse(
                tokenProvider.validateToken(
                        expiredToken
                )
        );
    }

    // =========================================================
    // INVALID SIGNATURE
    // =========================================================

    @Test
    @DisplayName("Deve rejeitar assinatura inválida")
    void testValidateTokenWithInvalidSignature() {

        String otherSecret =
                "outra-chave-super-secreta-de-32-bytes";

        String invalidToken =
                Jwts.builder()

                        .setSubject(
                                "teste@email.com"
                        )

                        .claim(
                                "userId",
                                userId.toString()
                        )

                        .claim(
                                "role",
                                "USER"
                        )

                        .setIssuedAt(
                                new Date()
                        )

                        .setExpiration(
                                new Date(
                                        System.currentTimeMillis()
                                                + 3600000
                                )
                        )

                        .signWith(
                                Keys.hmacShaKeyFor(
                                        otherSecret.getBytes(
                                                StandardCharsets.UTF_8
                                        )
                                ),
                                SignatureAlgorithm.HS256
                        )

                        .compact();

        assertFalse(
                tokenProvider.validateToken(
                        invalidToken
                )
        );
    }

    // =========================================================
    // EMPTY TOKEN
    // =========================================================

    @Test
    @DisplayName("Deve rejeitar token vazio")
    void testValidateEmptyToken() {

        assertFalse(
                tokenProvider.validateToken("")
        );
    }

    // =========================================================
    // NULL TOKEN
    // =========================================================

    @Test
    @DisplayName("Deve rejeitar token null")
    void testValidateNullToken() {

        assertFalse(
                tokenProvider.validateToken(null)
        );
    }

    // =========================================================
    // MALFORMED TOKEN
    // =========================================================

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