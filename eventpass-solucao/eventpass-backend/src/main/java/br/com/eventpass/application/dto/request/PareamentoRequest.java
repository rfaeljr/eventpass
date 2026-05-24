package br.com.eventpass.application.dto.request;

import jakarta.validation.constraints.*;

public record PareamentoRequest(
    @NotBlank String tokenPareamento,
    String modeloAndroid,
    String versaoApp
) {}
