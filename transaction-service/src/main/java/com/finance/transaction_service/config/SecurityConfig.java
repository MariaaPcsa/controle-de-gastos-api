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
                .csrf(csrf -> csrf.disable())

                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable())
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/h2-console/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        // 🔥 Agora funciona porque ROLE_ está correto
                        .requestMatchers("/api/transactions/**")
                        .hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )

                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint((req, res, ex) -> {
                                    res.setStatus(401);
                                    res.getWriter().write("{\"error\": \"Não autenticado\"}");
                                })
                                .accessDeniedHandler((req, res, ex) -> {
                                    res.setStatus(403);
                                    res.getWriter().write("{\"error\": \"Acesso negado\"}");
                                })
                );

        return http.build();
    }
}