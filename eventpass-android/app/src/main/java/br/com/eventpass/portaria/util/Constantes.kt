package br.com.eventpass.portaria.util

import android.content.SharedPreferences
import br.com.eventpass.portaria.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

object Constantes {
    val BASE_URL: String get() = BuildConfig.BASE_URL
    const val PREF_DEVICE_TOKEN = "device_token"
    const val PREF_EVENTO_ID    = "evento_id"
    const val PREF_EVENTO_NOME  = "evento_nome"
    const val DELAY_RESULTADO_MS = 3500L
}

@Singleton
class PreferenciasLocais @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun salvarDeviceToken(token: String) =
        prefs.edit().putString(Constantes.PREF_DEVICE_TOKEN, token).apply()

    fun getDeviceToken(): String? =
        prefs.getString(Constantes.PREF_DEVICE_TOKEN, null)

    fun estaPareado(): Boolean = getDeviceToken() != null

    fun salvarEvento(eventoId: Long, eventoNome: String) =
        prefs.edit()
            .putLong(Constantes.PREF_EVENTO_ID, eventoId)
            .putString(Constantes.PREF_EVENTO_NOME, eventoNome)
            .apply()

    fun getEventoId(): Long? =
        prefs.getLong(Constantes.PREF_EVENTO_ID, -1L).takeIf { it != -1L }

    fun getEventoNome(): String? =
        prefs.getString(Constantes.PREF_EVENTO_NOME, null)

    fun temEventoAssociado(): Boolean = getEventoId() != null

    fun limpar() = prefs.edit().clear().apply()
}
