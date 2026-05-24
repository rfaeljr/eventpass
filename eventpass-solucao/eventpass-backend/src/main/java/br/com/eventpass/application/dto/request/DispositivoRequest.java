package br.com.eventpass.application.dto.request;

import jakarta.validation.constraints.*;

public record DispositivoRequest(
    @NotBlank @Size(max=80) String descricao
) {}
