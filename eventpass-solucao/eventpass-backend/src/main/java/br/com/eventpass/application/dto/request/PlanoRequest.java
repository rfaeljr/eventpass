package br.com.eventpass.application.dto.request;

import br.com.eventpass.domain.enums.StatusEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record PlanoRequest (
        @NotNull Long clienteId,
        @NotNull Long planoId,
        @NotNull StatusEvento status,
        @NotBlank LocalDateTime iniciaEm,
        @NotBlank LocalDateTime expiraEm,
        @NotBlank LocalDateTime renovaEm,
        @Size(max=100) String mpAssinaturaId,
        @NotBlank LocalDateTime criadoEm
){};

