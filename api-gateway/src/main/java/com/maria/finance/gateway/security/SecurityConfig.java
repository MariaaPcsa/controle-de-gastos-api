package com.maria.finance.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
                // O Gateway possui autenticação própria via JwtFilter
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Desabilita HTTP Basic padrão
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                // Desabilita formulário de login padrão
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // O JwtFilter será responsável por validar o JWT
                .authorizeExchange(exchange -> exchange
                        .anyExchange()
                        .permitAll()
                )

                .build();
    }
}
