package com.maria.finance.user.domain.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserType type;
    private Boolean active; // ✅ ADICIONAR
    private LocalDateTime createdAt;

    public User(Long id, String name, String email, String password, UserType type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.active = true; // ✅ padrão
        this.createdAt = LocalDateTime.now();
    }

    public User(String name, String email, String password, UserType type) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return this.type == UserType.ADMIN;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
        if (updatedData.getActive() != null) this.active = updatedData.getActive();
    }
}
