// AdminInitializer.java
package com.maria.finance.user.infrastructure.security;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.model.UserType;
import com.maria.finance.user.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
//
    @Override
    public void run(String... args) {
        String adminEmail = "admin@finance.com";

        // Verifica se já existe o admin
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                admin -> System.out.println("ADMIN já existe: " + adminEmail),
                () -> {
                    User admin = new User(
                            null,
                            "Administrador",
                            adminEmail,
                            passwordEncoder.encode("admin123"),
                            UserType.ADMIN
                    );
                    userRepository.save(admin);
                    System.out.println("ADMIN padrão criado: " + adminEmail);
                }
        );
    }
}
