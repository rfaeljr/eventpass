package br.com.eventpass.portaria.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.eventpass.portaria.BuildConfig
import br.com.eventpass.portaria.data.model.*
import br.com.eventpass.portaria.data.repository.PortariaRepository
import br.com.eventpass.portaria.util.PreferenciasLocais
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// ══════════════════════════════════════════════════════════════════════════════
//  PareamentoViewModel
// ══════════════════════════════════════════════════════════════════════════════
@HiltViewModel
class PareamentoViewModel @Inject constructor(
    private val repository: PortariaRepository,
    val prefs: PreferenciasLocais
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<String>>(UiState.Idle)
    val estado: StateFlow<UiState<String>> = _estado.asStateFlow()

    fun processarQrPareamento(tokenPareamento: String) {
        if (_estado.value is UiState.Carregando) return
        viewModelScope.launch {
            _estado.value = UiState.Carregando
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.concluirPareamento(
                        tokenPareamento = tokenPareamento,
                        modeloAndroid   = "${Build.MANUFACTURER} ${Build.MODEL}",
                        versaoApp       = BuildConfig.VERSION_NAME
                    )
                }
                _estado.value = UiState.Sucesso(response.tokenDispositivo)
            } catch (e: Exception) {
                _estado.value = UiState.Erro(e.message ?: "Erro ao parear. Tente novamente.")
            }
        }
    }

    fun resetar() { _estado.value = UiState.Idle }
}

// ══════════════════════════════════════════════════════════════════════════════
//  PortariaViewModel
// ══════════════════════════════════════════════════════════════════════════════
@HiltViewModel
class PortariaViewModel @Inject constructor(
    private val repository: PortariaRepository,
    val prefs: PreferenciasLocais
) : ViewModel() {

    private val _resultado = MutableStateFlow<UiState<ResultadoLeituraResponse>>(UiState.Idle)
    val resultado: StateFlow<UiState<ResultadoLeituraResponse>> = _resultado.asStateFlow()

    private val _dashboard = MutableStateFlow<UiState<EventoInfoResponse>>(UiState.Idle)
    val dashboard: StateFlow<UiState<EventoInfoResponse>> = _dashboard.asStateFlow()

    private val _buscaResultados = MutableStateFlow<List<ConvidadoResponse>>(emptyList())
    val buscaResultados: StateFlow<List<ConvidadoResponse>> = _buscaResultados.asStateFlow()

    private var ultimoUuidProcessado: String? = null
    private var processando = false

    fun processarQrCode(uuid: String, qtdAcompanhantes: Int = 0) {
        if (processando || uuid == ultimoUuidProcessado) return
        processando = true
        ultimoUuidProcessado = uuid

        viewModelScope.launch {
            _resultado.value = UiState.Carregando
            try {
                val resp = withContext(Dispatchers.IO) {
                    repository.processarLeitura(uuid, qtdAcompanhantes)
                }
                _resultado.value = UiState.Sucesso(resp)
                atualizarDashboard()
            } catch (e: Exception) {
                _resultado.value = UiState.Erro(e.message ?: "Erro ao processar. Verifique a conexão.")
            } finally {
                processando = false
            }
        }
    }

    /** Registra entrada de convidado encontrado pela busca manual (sem QR Code). */
    fun registrarEntradaManual(uuid: String, qtdAcompanhantes: Int) {
        processando          = false
        ultimoUuidProcessado = null
        processarQrCode(uuid, qtdAcompanhantes)
    }

    fun buscarPorNome(nome: String) {
        if (nome.length < 3) return
        viewModelScope.launch {
            try {
                _buscaResultados.value = withContext(Dispatchers.IO) {
                    repository.buscarConvidadosPorNome(nome)
                }
            } catch (e: Exception) {
                _buscaResultados.value = emptyList()
            }
        }
    }

    fun atualizarDashboard() {
        viewModelScope.launch {
            try {
                val info = withContext(Dispatchers.IO) { repository.buscarDashboard() }
                _dashboard.value = UiState.Sucesso(info)
            } catch (_: Exception) { }
        }
    }

    fun voltarParaScanner() {
        _resultado.value     = UiState.Idle
        ultimoUuidProcessado = null
        processando          = false
    }

    fun limparBusca() { _buscaResultados.value = emptyList() }
}
