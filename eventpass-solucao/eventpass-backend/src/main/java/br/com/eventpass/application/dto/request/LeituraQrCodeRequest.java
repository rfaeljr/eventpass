package br.com.eventpass.application.dto.request;

import jakarta.validation.constraints.*;

public record LeituraQrCodeRequest(
    @NotBlank String uuid,
    Integer qtdAcompanhantes
) {}
