package br.com.eventpass.application.dto.request;

import jakarta.validation.constraints.*;

public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank String senha
) {}
