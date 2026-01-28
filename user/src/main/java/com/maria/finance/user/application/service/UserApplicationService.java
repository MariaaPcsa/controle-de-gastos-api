package com.maria.finance.user.application.service;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.domain.repository.UserRepository;
import com.maria.finance.user.domain.usecase.CreateUserUseCase;
import com.maria.finance.user.domain.usecase.DeleteUserUseCase;
import com.maria.finance.user.domain.usecase.ListUsersUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserApplicationService {

    private final CreateUserUseCase create;
    private final ListUsersUseCase list;
    private final DeleteUserUseCase delete;

    public UserApplicationService(UserRepository repository) {
        this.create = new CreateUserUseCase(repository);
        this.list = new ListUsersUseCase(repository);
        this.delete = new DeleteUserUseCase(repository);
    }

    public User create(User user) {
        return create.execute(user);
    }

    public List<User> list(User requester) {
        return list.execute(requester);
    }

    public void delete(Long id, User requester) {
        delete.execute(id, requester);
    }
}
