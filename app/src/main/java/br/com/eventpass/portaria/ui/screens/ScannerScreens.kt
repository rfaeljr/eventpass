package br.com.eventpass.portaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import br.com.eventpass.portaria.data.model.ResultadoLeituraResponse
import br.com.eventpass.portaria.data.model.UiState
import br.com.eventpass.portaria.ui.components.CameraQrScanner
import br.com.eventpass.portaria.ui.theme.EventPassColors
import br.com.eventpass.portaria.viewmodel.PortariaViewModel
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════════════════
//  SCANNER SCREEN — câmera em tela cheia
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun ScannerScreen(
    onResultado: () -> Unit,
    onVoltar: () -> Unit,
    vm: PortariaViewModel = hiltViewModel()
) {
    val resultado by vm.resultado.collectAsState()

    LaunchedEffect(resultado) {
        if (resultado is UiState.Sucesso || resultado is UiState.Erro) {
            onResultado()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraQrScanner(
            onQrDetectado = { uuid ->
                if (resultado is UiState.Idle) {
                    vm.processarQrCode(uuid)
                }
            }
        )

        // Loading enquanto API processa
        if (resultado is UiState.Carregando) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(52.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.validando_entrada), color = Color.White, fontSize = 16.sp)
                }
            }
        }

        // Botão voltar
        IconButton(
            onClick  = onVoltar,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(50))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.voltar), tint = Color.White)
        }

        // Rodapé — busca manual
        Box(
            modifier         = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.msg_qr_nao_funciona),
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  RESULTADO SCREEN — Verde / Amarelo / Vermelho
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun ResultadoScreen(
    onProximo: () -> Unit,
    vm: PortariaViewModel = hiltViewModel()
) {
    val resultado by vm.resultado.collectAsState()

    // Auto-volta após 3,5s para entradas liberadas sem reentrada
    LaunchedEffect(resultado) {
        if (resultado is UiState.Sucesso) {
            val dados = (resultado as UiState.Sucesso<ResultadoLeituraResponse>).dados
            if (dados.liberado && dados.status != "REENTRADA") {
                delay(3500)
                vm.voltarParaScanner()
                onProximo()
            }
        }
    }

    when (val r = resultado) {
        is UiState.Sucesso -> ResultadoConteudo(
            dados    = r.dados,
            onProximo = { vm.voltarParaScanner(); onProximo() }
        )
        is UiState.Erro -> ErroConexao(
            mensagem = r.mensagem,
            onVoltar = { vm.voltarParaScanner(); onProximo() }
        )
        else -> Box(
            Modifier.fillMaxSize().background(EventPassColors.Background),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator(color = EventPassColors.Primario) }
    }
}

// ── Conteúdo principal do resultado ──────────────────────────────────────────
@Composable
private fun ResultadoConteudo(dados: ResultadoLeituraResponse, onProximo: () -> Unit) {
    var acompanhantes by remember { mutableStateOf(0) }

    val corFundo = when {
        dados.status == "REENTRADA"         -> EventPassColors.Amarelo
        dados.liberado                       -> EventPassColors.Verde
        else                                 -> EventPassColors.Vermelho
    }
    val emoji  = when { dados.status == "REENTRADA" -> "⚠️"; dados.liberado -> "✅"; else -> "❌" }
    val titulo = when {
        dados.status == "REENTRADA" -> stringResource(R.string.ja_entrou)
        dados.liberado               -> stringResource(R.string.entrada_liberada)
        else                         -> stringResource(R.string.entrada_negada)
    }

    Box(
        modifier         = Modifier.fillMaxSize().background(corFundo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 96.sp)
            Spacer(Modifier.height(12.dp))

            Text(titulo, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)

            dados.convidadoNome?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
            dados.grupoTag?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            }

            // Seletor de acompanhantes
            val maxAcomp = dados.maxAcompanhantes ?: 0
            if (dados.liberado && maxAcomp > 0) {
                Spacer(Modifier.height(24.dp))
                Text(stringResource(R.string.label_acompanhantes), fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (n in 0..maxAcomp) {
                        Button(
                            onClick = { acompanhantes = n },
                            modifier = Modifier.size(48.dp),
                            shape    = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor = if (acompanhantes == n) Color.White
                                                 else Color.White.copy(alpha = 0.22f),
                                contentColor   = if (acompanhantes == n) corFundo else Color.White
                            )
                        ) { Text(n.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }

            if (dados.liberado && dados.status != "REENTRADA") {
                Spacer(Modifier.height(24.dp))
                Text(stringResource(R.string.msg_voltando_scanner), fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
            }

            // Botões para reentrada / negado
            if (!dados.liberado || dados.status == "REENTRADA") {
                Spacer(Modifier.height(32.dp))
                if (dados.status == "REENTRADA") {
                    Button(
                        onClick  = onProximo,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = corFundo)
                    ) { Text(stringResource(R.string.btn_permitir_reentrada), fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedButton(
                    onClick  = onProximo,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(14.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) { Text(stringResource(R.string.proximo)) }
            }
        }
    }
}

// ── Tela de erro de conexão ───────────────────────────────────────────────────
@Composable
private fun ErroConexao(mensagem: String, onVoltar: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(EventPassColors.Vermelho),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("❌", fontSize = 72.sp)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.titulo_erro_conexao), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(mensagem, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            Button(
                onClick  = onVoltar,
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = EventPassColors.Vermelho)
            ) { Text(stringResource(R.string.tentar_novamente)) }
        }
    }
}
