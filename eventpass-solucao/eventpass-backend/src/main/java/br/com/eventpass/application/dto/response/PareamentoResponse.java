package br.com.eventpass.application.dto.response;

import java.time.LocalDateTime;

public record PareamentoResponse(
    Long dispositivoId,
    String descricao,
    String tokenPareamento,
    LocalDateTime expiraEm
) {}
