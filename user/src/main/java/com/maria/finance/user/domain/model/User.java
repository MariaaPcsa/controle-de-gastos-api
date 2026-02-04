package com.maria.finance.user.domain.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserType type;
    private LocalDateTime createdAt;

    // 🔹 Construtor principal (usado para objetos completos)
    public User(Long id, String name, String email, String password, UserType type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // 🔹 Construtor para criação (sem id)
    public User(String name, String email, String password, UserType type) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.createdAt = LocalDateTime.now(); // ✅ corrigido
    }

    // 🔹 Construtor vazio
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return this.type == UserType.ADMIN;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setType(UserType type) { this.type = type; }

    public void update(User updatedData) {
        if (updatedData.getName() != null) this.name = updatedData.getName();
        if (updatedData.getEmail() != null) this.email = updatedData.getEmail();
        if (updatedData.getPassword() != null) this.password = updatedData.getPassword();
        if (updatedData.getType() != null) this.type = updatedData.getType();
    }
}
