package com.finance.transaction_service.config;

import com.finance.transaction_service.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter) {

        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .headers(headers ->
                        headers.frameOptions(frame ->
                                frame.disable()
                        )
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ============================
                        // ROTAS PÚBLICAS
                        // ============================

                        .requestMatchers(
                                "/api/auth/**",
                                "/h2-console/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/actuator/**"
                        ).permitAll()

                        // ============================
                        // TRANSAÇÕES
                        // USER + ADMIN
                        // ============================

                        .requestMatchers(
                                "/api/transactions/**"
                        ).hasAnyRole(
                                "USER",
                                "ADMIN"
                        )

                        // ============================
                        // QUALQUER OUTRA ROTA
                        // ============================

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .exceptionHandling(exception ->
                        exception

                                .authenticationEntryPoint(
                                        (req, res, ex) -> {

                                            res.setStatus(401);
                                            res.setContentType(
                                                    "application/json"
                                            );

                                            res.getWriter().write(
                                                    "{\"error\":\"Não autenticado\"}"
                                            );
                                        }
                                )

                                .accessDeniedHandler(
                                        (req, res, ex) -> {

                                            res.setStatus(403);
                                            res.setContentType(
                                                    "application/json"
                                            );

                                            res.getWriter().write(
                                                    "{\"error\":\"Acesso negado\"}"
                                            );
                                        }
                                )
                );

        return http.build();
    }
}