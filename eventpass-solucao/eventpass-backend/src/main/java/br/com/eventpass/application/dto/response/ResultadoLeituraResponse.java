package br.com.eventpass.application.dto.response;

import br.com.eventpass.domain.enums.StatusEntrada;

public record ResultadoLeituraResponse(
    boolean liberado,
    String convidadoNome,
    String grupoTag,
    Integer maxAcompanhantes,
    Integer qtdAcompanhantes,
    StatusEntrada status,
    String mensagem
) {
    public static ResultadoLeituraResponse liberado(String nome, String grupo,
            int maxAcomp, int qtdAcomp, StatusEntrada status) {
        return new ResultadoLeituraResponse(true, nome, grupo, maxAcomp, qtdAcomp, status, "Entrada liberada.");
    }

    public static ResultadoLeituraResponse negado(String mensagem) {
        return new ResultadoLeituraResponse(false, null, null, null, null, StatusEntrada.NEGADO, mensagem);
    }
}
