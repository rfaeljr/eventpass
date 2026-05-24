package br.com.eventpass.application.dto.request;

import jakarta.validation.constraints.*;

public record VerificarOtpRequest(
    @NotBlank @Size(min=6, max=6) String codigo,
    @NotBlank @Size(min=12, max=13) String telefone
) {}
