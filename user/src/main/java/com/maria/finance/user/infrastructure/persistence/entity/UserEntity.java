package com.maria.finance.user.infrastructure.persistence.entity;

import com.maria.finance.user.domain.model.UserType;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType type;

    // 🔹 CONSTRUTOR PADRÃO (OBRIGATÓRIO PARA JPA)
    public UserEntity() {
    }

    // 🔹 CONSTRUTOR DE CONVENIÊNCIA
    public UserEntity(Long id, String name, String email, String password, UserType type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;


    // getters e setters
}


    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserType getType() { return type; }
    public void setType(UserType type) { this.type = type; }
}
