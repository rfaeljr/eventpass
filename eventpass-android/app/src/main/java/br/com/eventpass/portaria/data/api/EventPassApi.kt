package br.com.eventpass.portaria.data.api

import br.com.eventpass.portaria.data.model.*
import retrofit2.http.*

interface EventPassApi {

    /** POST /dispositivos/pareamento/concluir */
    @POST("dispositivos/pareamento/concluir")
    suspend fun concluirPareamento(
        @Body request: PareamentoRequest
    ): PareamentoResponse

    /** POST /portaria/leitura — autenticado via X-Device-Token */
    @POST("portaria/leitura")
    suspend fun processarLeitura(
        @Header("X-Device-Token") deviceToken: String,
        @Body request: LeituraQrCodeRequest
    ): ResultadoLeituraResponse

    /** GET /portaria/buscar?eventoId=2&nome=joao */
    @GET("portaria/buscar")
    suspend fun buscarPorNome(
        @Header("X-Device-Token") deviceToken: String,
        @Query("eventoId") eventoId: Long,
        @Query("nome") nome: String
    ): List<ConvidadoResponse>

    /** GET /eventos/{id}/dashboard */
    @GET("eventos/{id}/dashboard")
    suspend fun buscarDashboard(
        @Header("X-Device-Token") deviceToken: String,
        @Path("id") eventoId: Long
    ): EventoInfoResponse
}
