package com.maria.finance.user.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // aqui é a forma nova
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // libera todas as rotas
                )
                .httpBasic(Customizer.withDefaults()); // opcional, só para testes
        return http.build();
    }


//                .csrf(csrf -> csrf.disable()) // desativa CSRF
//                .authorizeHttpRequests(auth -> auth
//                        // 🔓 Endpoints públicos: login e registro
//                        .requestMatchers("/auth/login", "/auth/register").permitAll()
//
//                        // 🔓 Swagger
//                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//
//                        // 🔒 resto da API protegido
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(); // login básico para endpoints protegidos

//        return http.build();
    }

