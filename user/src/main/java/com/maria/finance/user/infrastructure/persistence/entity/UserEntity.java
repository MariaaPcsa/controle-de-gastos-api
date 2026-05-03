package com.maria.finance.user.infrastructure.persistence.entity;

import com.maria.finance.user.domain.model.UserType;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET active = false WHERE id = ?")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ✅ Construtor padrão (JPA)
    public UserEntity() {}

    // 🔥 Garante valores antes de salvar
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.active == null) {
            this.active = true;
        }

        if (this.type == null) {
            this.type = UserType.USER; // 🔒 segurança por padrão
        }
    }

    // 🔥 Atualiza updatedAt automaticamente
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();

        if (this.active == null) {
            this.active = true; // nunca deixar null
        }
    }

    // GETTERS E SETTERS
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

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
