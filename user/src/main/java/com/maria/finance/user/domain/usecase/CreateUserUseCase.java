package com.maria.finance.user.domain.usecase;



import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;

public class CreateUserUseCase {

    private final UserRepository repository;

    public CreateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new RuntimeException("Email inválido");
        }
    }

    public User execute(User user) {
        // Verifica se o email já existe
        repository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email já cadastrado");
                });


        return repository.save(user);
    }

}

