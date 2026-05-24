package br.com.eventpass.application.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record EventoRequest(
    @NotBlank @Size(max=150) String nome,
    String descricao,
    String local,
    @NotNull LocalDateTime iniciaEm,
    @NotNull LocalDateTime terminaEm,
    Integer capacidadeMaxima,
    Integer maxAcompanhantes,
    Boolean permiteReentrada
) {}
