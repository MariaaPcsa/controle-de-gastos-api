package com.maria.finance.user.application.service;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import com.maria.finance.user.domain.usecase.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserApplicationService {

    private final CreateUserUseCase create;
    private final ListUsersUseCase list;
    private final DeleteUserUseCase delete;
    private final UpdateUserUseCase update;
    private final FindUserByEmailUseCase findByEmail;
    private final FindUserByIdUseCase findById;

    public UserApplicationService(UserRepository repository) {
        this.create = new CreateUserUseCase(repository);
        this.list = new ListUsersUseCase(repository);
        this.delete = new DeleteUserUseCase(repository);
        this.update = new UpdateUserUseCase(repository);
        this.findByEmail = new FindUserByEmailUseCase(repository);
        this.findById = new FindUserByIdUseCase(repository);
    }

    // 🔹 CREATE
    public User create(User user) {
        return create.execute(user);
    }

    // 🔹 LIST
    public List<User> list(User requester) {
        return list.execute(requester);
    }

    // 🔹 FIND BY ID (delegando pro UseCase)
    public User findById(Long id, User requester) {
        return findById.execute(id, requester);
    }

    // 🔹 DELETE
    public void delete(Long id, User requester) {
        delete.execute(id, requester);
    }

    // 🔹 UPDATE
    public User update(Long id, User userData, User requester) {
        return update.execute(id, userData, requester);
    }

    // 🔹 FIND BY EMAIL
    public Optional<User> findByEmail(String email) {
        return findByEmail.execute(email);
    }

    // 🔹 CREATE OR UPDATE (IMPORTAÇÃO EXCEL)
    public User createOrUpdate(User user, User requester) {
        if (!requester.isAdmin()) {
            throw new RuntimeException("Apenas ADMIN pode importar usuários");
        }

        Optional<User> existing = findByEmail(user.getEmail());

        if (existing.isPresent()) {
            return update(existing.get().getId(), user, requester);
        }

        return create(user);
    }
}
