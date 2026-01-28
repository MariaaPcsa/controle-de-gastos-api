//package com.maria.finance.user.domain.usecase;
//
//
//
//import com.maria.finance.user.domain.model.User;
//import com.maria.finance.user.domain.model.UserType;
//import com.maria.finance.user.domain.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//class ListUsersUseCaseTest {
//
//    private UserRepository repository;
//    private ListUsersUseCase useCase;
//
//    @BeforeEach
//    void setup() {
//        repository = Mockito.mock(UserRepository.class);
//        useCase = new ListUsersUseCase(repository);
//    }
//
//    @Test
//    void userComumVeApenasASiMesmo() {
//        User user = new User(
//                2L,
//                "User",
//                "u@email.com",
//                "123",
//                UserType.USER
//        );
//
//        when(repository.findById(2L))
//                .thenReturn(Optional.of(user));
//
//        List<User> result = useCase.execute(user);
//
//        assertEquals(1, result.size());
//        assertEquals("User", result.get(0).getName());
//    }
//
//}
