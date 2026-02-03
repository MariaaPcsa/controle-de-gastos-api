package com.maria.finance.user.application.service;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import com.maria.finance.user.domain.usecase.*;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService {

    private final CreateUserUseCase create;
    private final ListUsersUseCase list;
    private final DeleteUserUseCase delete;
    private final UpdateUserUseCase update;
    private final FindUserByEmailUseCase findByEmail;

    public UserApplicationService(UserRepository repository) {
        this.create = new CreateUserUseCase(repository);
        this.list = new ListUsersUseCase(repository);
        this.delete = new DeleteUserUseCase(repository);
        this.update = new UpdateUserUseCase(repository);
        this.findByEmail = new FindUserByEmailUseCase(repository);
    }

    // 🔹 Create
    public User create(User user) {
        return create.execute(user);
    }

    // 🔹 List
    public List<User> list(User requester) {
        return list.execute(requester);
    }

    // 🔹 Delete
    public void delete(Long id, User requester) {
        delete.execute(id, requester);
    }

    // 🔹 Update
    public User update(Long id, User userData, User requester) {
        return update.execute(id, userData, requester);
    }

    // 🔹 Find by email
    public Optional<User> findByEmail(String email) {
        return findByEmail.execute(email);
    }

    // 🔹 Create or Update (para importar do Excel)
    public User createOrUpdate(User user, User requester) {
        if (!requester.isAdmin()) {
            throw new RuntimeException("Apenas ADMIN pode importar usuários");
        }

        // Verifica se já existe usuário com o mesmo email
        Optional<User> existing = findByEmail(user.getEmail());

        if (existing.isPresent()) {
            // Atualiza o usuário existente
            return update(existing.get().getId(), user, requester);
        } else {

            return create(user);
        }
    }
}
