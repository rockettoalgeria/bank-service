package com.example.bankservice.model;

public class JWTResponse {

    private final String token;

    public JWTResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
