package br.com.codenest.marknow.dto.auth;

public class LoginResponse {
    public String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}
