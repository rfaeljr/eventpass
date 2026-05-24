package br.com.eventpass.portaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.eventpass.portaria.R
import br.com.eventpass.portaria.data.model.UiState
import br.com.eventpass.portaria.ui.components.CameraQrScanner
import br.com.eventpass.portaria.ui.theme.EventPassColors
import br.com.eventpass.portaria.viewmodel.PareamentoViewModel
import kotlinx.coroutines.delay

// ── Splash ────────────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(
    onTemToken: () -> Unit,
    onSemToken: () -> Unit,
    vm: PareamentoViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(1400)
        if (vm.prefs.estaPareado()) onTemToken() else onSemToken()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(EventPassColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎉", fontSize = 72.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "EventPass", fontSize = 28.sp,
                fontWeight = FontWeight.Bold, color = EventPassColors.TextoPrimario
            )
            Text("Portaria", fontSize = 16.sp, color = EventPassColors.TextoSecundario)
            Spacer(Modifier.height(48.dp))
            CircularProgressIndicator(color = EventPassColors.Primario)
        }
    }
}

// ── Pareamento ────────────────────────────────────────────────────────────────
@Composable
fun PareamentoScreen(
    onPareado: () -> Unit,
    vm: PareamentoViewModel = hiltViewModel()
) {
    val estado by vm.estado.collectAsState()

    LaunchedEffect(estado) {
        if (estado is UiState.Sucesso) { delay(800); onPareado() }
    }

    Box(Modifier.fillMaxSize().background(EventPassColors.Background)) {
        when (estado) {
            is UiState.Carregando -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = EventPassColors.Primario)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.msg_vinculando), color = EventPassColors.TextoSecundario)
                }
            }
            is UiState.Sucesso -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("✅", fontSize = 72.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.msg_vinculado), fontSize = 22.sp,
                        fontWeight = FontWeight.Bold, color = EventPassColors.VerdeTexto
                    )
                }
            }
            else -> {
                Column(Modifier.fillMaxSize()) {
                    // Cabeçalho
                    Box(
                        Modifier.fillMaxWidth().background(EventPassColors.Surface).padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔗", fontSize = 28.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                stringResource(R.string.pareamento_titulo), fontSize = 18.sp,
                                fontWeight = FontWeight.Bold, color = EventPassColors.TextoPrimario
                            )
                            Text(
                                stringResource(R.string.pareamento_instrucao), fontSize = 13.sp,
                                color = EventPassColors.TextoSecundario, textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Scanner
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        CameraQrScanner(onQrDetectado = { vm.processarQrPareamento(it) })
                    }

                    // Erro
                    if (estado is UiState.Erro) {
                        Card(
                            Modifier.fillMaxWidth().padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = EventPassColors.Vermelho.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("❌", fontSize = 20.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(stringResource(R.string.msg_falha_pareamento), fontWeight = FontWeight.Bold, color = EventPassColors.VermelhoTexto)
                                    Text(
                                        (estado as UiState.Erro).mensagem, fontSize = 12.sp,
                                        color = EventPassColors.VermelhoTexto.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                            TextButton(onClick = { vm.resetar() }) {
                                Text(stringResource(R.string.tentar_novamente), color = EventPassColors.Primario)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Confirmação ───────────────────────────────────────────────────────────────
@Composable
fun ConfirmacaoScreen(onContinuar: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(EventPassColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🟢", fontSize = 64.sp)
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.msg_vinculado), fontSize = 22.sp,
                fontWeight = FontWeight.Bold, color = EventPassColors.TextoPrimario
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.msg_aguarde_admin),
                fontSize = 14.sp, color = EventPassColors.TextoSecundario, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = onContinuar,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EventPassColors.Primario)
            ) {
                Text(stringResource(R.string.btn_continuar), fontSize = 16.sp)
            }
        }
    }
}
