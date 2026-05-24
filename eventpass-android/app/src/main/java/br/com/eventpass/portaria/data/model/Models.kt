package br.com.eventpass.portaria.data.model

import com.google.gson.annotations.SerializedName

// ── Requests ──────────────────────────────────────────────────────────────────

data class PareamentoRequest(
    @SerializedName("tokenPareamento") val tokenPareamento: String,
    @SerializedName("modeloAndroid")   val modeloAndroid: String? = null,
    @SerializedName("versaoApp")       val versaoApp: String?     = null
)

data class LeituraQrCodeRequest(
    @SerializedName("uuid")             val uuid: String,
    @SerializedName("qtdAcompanhantes") val qtdAcompanhantes: Int = 0
)

// ── Responses ─────────────────────────────────────────────────────────────────

data class PareamentoResponse(
    @SerializedName("tokenDispositivo") val tokenDispositivo: String
)

data class ResultadoLeituraResponse(
    @SerializedName("liberado")         val liberado: Boolean,
    @SerializedName("convidadoNome")    val convidadoNome: String?,
    @SerializedName("grupoTag")         val grupoTag: String?,
    @SerializedName("maxAcompanhantes") val maxAcompanhantes: Int?,
    @SerializedName("qtdAcompanhantes") val qtdAcompanhantes: Int?,
    @SerializedName("status")           val status: String,
    @SerializedName("mensagem")         val mensagem: String
)

data class ConvidadoResponse(
    @SerializedName("id")            val id: Long,
    @SerializedName("nome")          val nome: String,
    @SerializedName("documento")     val documento: String?,
    @SerializedName("grupoTag")      val grupoTag: String?,
    @SerializedName("statusConvite") val statusConvite: String
)

data class EventoInfoResponse(
    @SerializedName("eventoNome")         val eventoNome: String,
    @SerializedName("eventoStatus")       val eventoStatus: String,
    @SerializedName("totalEntradas")      val totalEntradas: Long,
    @SerializedName("totalConvidados")    val totalConvidados: Long,
    @SerializedName("capacidadeMaxima")   val capacidadeMaxima: String?,
    @SerializedName("percentualOcupacao") val percentualOcupacao: String
)

// ── Estado das telas ──────────────────────────────────────────────────────────

sealed class UiState<out T> {
    object Idle       : UiState<Nothing>()
    object Carregando : UiState<Nothing>()
    data class Sucesso<T>(val dados: T)   : UiState<T>()
    data class Erro(val mensagem: String) : UiState<Nothing>()
}
