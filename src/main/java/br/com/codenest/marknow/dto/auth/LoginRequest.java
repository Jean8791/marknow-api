package br.com.codenest.marknow.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    public String email;

    @NotBlank
    public String senha;
}