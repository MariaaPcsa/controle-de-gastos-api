package com.maria.finance.user.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private String email;
    private String password;
    private UserType type;
    private Boolean active;
    private LocalDateTime createdAt;

    public User(UUID id, String name, String email, String password, UserType type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.active = true;
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

    // 🔥 ADICIONAR ISSO
    public boolean isAdmin() {
        return this.type == UserType.ADMIN;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserType getType() { return type; }
    public Boolean getActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setType(UserType type) { this.type = type; }
    public void setActive(Boolean active) { this.active = active; }
}