package com.maria.finance.user.presentation.dto;

public class AuthResponseDTO {

    private String token;
    private Long userId;
    private String name;
    private String email;
    private String type;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public AuthResponseDTO(String token, Long userId, String name, String email, String type) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getType() { return type; }

    public void setToken(String token) { this.token = token; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setType(String type) { this.type = type; }
}
