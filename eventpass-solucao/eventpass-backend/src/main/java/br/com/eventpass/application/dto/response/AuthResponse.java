package br.com.eventpass.application.dto.response;

public record AuthResponse(
    String token,
    String nome,
    String email,
    boolean emTrial
) {}
