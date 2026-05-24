package br.com.eventpass.portaria.data.repository

import br.com.eventpass.portaria.data.api.EventPassApi
import br.com.eventpass.portaria.data.model.*
import br.com.eventpass.portaria.util.PreferenciasLocais
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortariaRepository @Inject constructor(
    private val api:   EventPassApi,
    private val prefs: PreferenciasLocais
) {
    // ── Pareamento ─────────────────────────────────────────────────────────────
    suspend fun concluirPareamento(
        tokenPareamento: String,
        modeloAndroid:   String?,
        versaoApp:       String?
    ): PareamentoResponse {
        val response = api.concluirPareamento(
            PareamentoRequest(tokenPareamento, modeloAndroid, versaoApp)
        )
        prefs.salvarDeviceToken(response.tokenDispositivo)
        return response
    }

    // ── Portaria ───────────────────────────────────────────────────────────────
    suspend fun processarLeitura(uuid: String, qtdAcompanhantes: Int): ResultadoLeituraResponse {
        val token = prefs.getDeviceToken()
            ?: error("Dispositivo não pareado.")
        return api.processarLeitura(token, LeituraQrCodeRequest(uuid, qtdAcompanhantes))
    }

    suspend fun buscarConvidadosPorNome(nome: String): List<ConvidadoResponse> {
        val token    = prefs.getDeviceToken() ?: error("Dispositivo não pareado.")
        val eventoId = prefs.getEventoId()    ?: error("Nenhum evento associado.")
        return api.buscarPorNome(token, eventoId, nome)
    }

    suspend fun buscarDashboard(): EventoInfoResponse {
        val token    = prefs.getDeviceToken() ?: error("Dispositivo não pareado.")
        val eventoId = prefs.getEventoId()    ?: error("Nenhum evento associado.")
        return api.buscarDashboard(token, eventoId)
    }
}
