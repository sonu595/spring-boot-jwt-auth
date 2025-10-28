package com.example.demo;

import lombok.Data;

@Data
public class AuthResponse {
    private String message;
    private Long userId;
    private String username;
    private String email;
    private String token;
    
    public AuthResponse(String message, Long userId, String username, String email, String token) {
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.token = token;
    }
    
    public AuthResponse(String message) {
        this.message = message;
        this.token = null;
    }
}