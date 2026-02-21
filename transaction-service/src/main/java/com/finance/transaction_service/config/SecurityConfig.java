package com.finance.transaction_service.config;

import com.finance.transaction_service.security.JwtAuthenticationFilter;
import com.finance.transaction_service.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;

    public SecurityConfig(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable())

                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🔐 Configurar autorização de endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sem autenticação)
                        .requestMatchers(
                                "/h2-console/**",
                                "/favicon.ico",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Endpoints que requerem autenticação com role
                        .requestMatchers("/api/transactions/**")
                        .hasAnyRole("USER", "ADMIN")

                        // Qualquer outra requisição requer autenticação
                        .anyRequest().authenticated()
                )

                // 🔐 Adicionar filter JWT ANTES do UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )

                // Tratamento de exceções de autenticação
                .exceptionHandling(exception ->
                    exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.err.println("❌ Falha de autenticação: " + authException.getMessage());
                            response.setStatus(401);
                            response.getWriter().write("{\"error\": \"Não autenticado\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.err.println("❌ Acesso negado: " + accessDeniedException.getMessage());
                            response.setStatus(403);
                            response.getWriter().write("{\"error\": \"Acesso negado\"}");
                        })
                );

        return http.build();
    }
}

