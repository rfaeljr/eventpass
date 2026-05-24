package br.com.eventpass.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConvidadoRequest(
    @NotBlank @Size(max = 150) String nome,
    String documento,
    String telefone,
    String email,
    String grupoTag,
    Integer maxAcompanhantes
) {}
