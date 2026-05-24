package br.com.eventpass.application.dto.request;

import jakarta.validation.constraints.*;

public record CadastroRequest(
    @NotBlank @Size(min=3, max=120) String nome,
    @NotBlank @Pattern( regexp="\\d{11}" ) String cpf,
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp="\\d{12,13}") String telefoneWhatsapp,
    @NotBlank @Size(min=8) String senha
) {}
