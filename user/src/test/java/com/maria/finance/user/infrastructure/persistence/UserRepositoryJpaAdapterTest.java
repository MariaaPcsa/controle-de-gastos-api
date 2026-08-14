//package com.maria.finance.user.infrastructure.persistence;
//
//import com.maria.finance.user.domain.model.User;
//import com.maria.finance.user.domain.model.UserType;
//import com.maria.finance.user.domain.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//@ActiveProfiles("test")
//@Transactional
//class UserRepositoryJpaAdapterTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    void deveSalvarEBuscarUsuarioPorEmail() {
//        User user = new User(
//                null,
//                "Maria",
//                "maria@email.com",
//                "123456",
//                UserType.USER
//        );
//
//        User saved = userRepository.save(user);
//
//        Optional<User> found = userRepository.findByEmail("maria@email.com");
//
//        assertTrue(found.isPresent());
//        assertEquals(saved.getId(), found.get().getId());
//    }
//}