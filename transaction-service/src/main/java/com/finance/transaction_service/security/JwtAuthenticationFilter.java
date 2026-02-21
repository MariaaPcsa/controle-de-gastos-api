package com.finance.transaction_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 🔥 Ignorar H2 e Swagger
        if (uri.startsWith("/h2-console")
                || uri.startsWith("/favicon.ico")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")) {

            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("==== FILTRO JWT EXECUTADO ====");
        System.out.println("URI: " + uri);

        String header = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + (header != null ? "Presente" : "Ausente"));

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            System.out.println("Token extraído: " + token.substring(0, Math.min(20, token.length())) + "...");

            try {
                if (tokenProvider.validateToken(token)) {
                    System.out.println("✅ Token válido!");

                    String username = tokenProvider.getUsername(token);
                    String role = tokenProvider.getRole(token);
                    Long userId = tokenProvider.getId(token);

                    System.out.println("Username: " + username);
                    System.out.println("Role: " + role);
                    System.out.println("User ID: " + userId);

                    if (username != null && !username.isEmpty() && role != null && !role.isEmpty() && userId != null) {
                        // Criar CustomUserDetails com id, username e role
                        CustomUserDetails userDetails = new CustomUserDetails(userId, username, role);

                        System.out.println("CustomUserDetails criado com ID: " + userId + ", Username: " + username + ", Role: " + role);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,  // Usar CustomUserDetails como principal
                                        null,
                                        userDetails.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("✅ Autenticação configurada no SecurityContext com CustomUserDetails");
                    } else {
                        System.err.println("❌ Username, Role ou ID nulo/vazio. Username: " + username + ", Role: " + role + ", ID: " + userId);
                    }
                } else {
                    System.err.println("❌ Token inválido ou expirado!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro ao validar token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️  Nenhum token Bearer encontrado no header");
        }

        filterChain.doFilter(request, response);
    }
}