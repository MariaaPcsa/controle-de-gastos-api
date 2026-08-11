package com.finance.transaction_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(
            JwtTokenProvider tokenProvider) {

        this.tokenProvider = tokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String path = request.getRequestURI();

        return path.startsWith("/api/auth")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header =
                request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();

        if (token.isBlank()) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            if (tokenProvider.validateToken(token)) {

                Claims claims =
                        tokenProvider.getClaimsSafe(token);

                if (claims != null) {

                    String username =
                            tokenProvider.getUsername(claims);

                    String role =
                            tokenProvider.getRole(claims);

                    UUID userId =
                            tokenProvider.getUserId(claims);

                    if (username != null
                            && role != null
                            && userId != null) {

                        CustomUserDetails userDetails =
                                new CustomUserDetails(
                                        userId,
                                        username,
                                        role
                                );

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authentication);

                        log.debug(
                                "JWT autenticado: username={}, userId={}, role={}",
                                username,
                                userId,
                                role
                        );
                    }
                }

            } else {

                log.warn("Token JWT inválido");
            }

        } catch (Exception e) {

            log.warn(
                    "Não foi possível processar o JWT: {}",
                    e.getMessage()
            );
        }

        /*
         * Importante:
         *
         * O filtro não trata erro de validação do DTO.
         * O Spring Security continua o processamento.
         */
        filterChain.doFilter(request, response);
    }
}