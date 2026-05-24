package br.com.eventpass.portaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.eventpass.portaria.R
import br.com.eventpass.portaria.data.model.EventoInfoResponse
import br.com.eventpass.portaria.data.model.UiState
import br.com.eventpass.portaria.ui.theme.EventPassColors
import br.com.eventpass.portaria.viewmodel.PortariaViewModel

@Composable
fun HomeScreen(
    onIniciarLeitura: () -> Unit,
    onBuscarPorNome: () -> Unit,
    vm: PortariaViewModel = hiltViewModel()
) {
    val dashboard  by vm.dashboard.collectAsState()
    val eventoNome = vm.prefs.getEventoNome() ?: stringResource(R.string.nenhum_evento_associado)

    LaunchedEffect(Unit) { vm.atualizarDashboard() }

    Column(
        Modifier.fillMaxSize().background(EventPassColors.Background)
    ) {
        // Header
        Box(Modifier.fillMaxWidth().background(EventPassColors.Surface).padding(20.dp)) {
            Column {
                Text(stringResource(R.string.app_portaria_header), fontSize = 13.sp, color = EventPassColors.TextoSecundario)
                Text(eventoNome, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = EventPassColors.TextoPrimario)
            }
        }

        // Dashboard
        when (val d = dashboard) {
            is UiState.Sucesso   -> DashboardCards(d.dados)
            is UiState.Carregando -> Box(
                Modifier.fillMaxWidth().padding(32.dp), Alignment.Center
            ) { CircularProgressIndicator(Modifier.size(28.dp), color = EventPassColors.Primario) }
            else -> Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.weight(1f))

        // Botões
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick  = onIniciarLeitura,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = EventPassColors.Primario)
            ) {
                Text(stringResource(R.string.iniciar_leitura), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick  = onBuscarPorNome,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(16.dp),
                border   = androidx.compose.foundation.BorderStroke(1.dp, EventPassColors.Divisor),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = EventPassColors.TextoSecundario)
            ) {
                Text(stringResource(R.string.buscar_por_nome), fontSize = 15.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DashboardCards(info: EventoInfoResponse) {
    val ocupacao = (info.percentualOcupacao.toFloatOrNull() ?: 0f) / 100f

    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ContadorCard(Modifier.weight(1f), "🚪", info.totalEntradas.toString(), stringResource(R.string.label_entradas))
            ContadorCard(Modifier.weight(1f), "👥", info.totalConvidados.toString(), stringResource(R.string.label_convidados))
        }

        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EventPassColors.Surface),
            shape  = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.label_ocupacao), fontSize = 12.sp, color = EventPassColors.TextoSecundario)
                    Text(
                        "${info.percentualOcupacao}%", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = when { ocupacao >= .9f -> EventPassColors.VermelhoTexto; ocupacao >= .7f -> EventPassColors.AmareloTexto; else -> EventPassColors.VerdeTexto }
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { ocupacao },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color    = when { ocupacao >= .9f -> EventPassColors.VermelhoClear; ocupacao >= .7f -> EventPassColors.AmareloClear; else -> EventPassColors.VerdeClear },
                    trackColor = EventPassColors.Divisor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(
                        R.string.label_capacidade,
                        info.capacidadeMaxima ?: stringResource(R.string.label_ilimitado)
                    ),
                    fontSize = 11.sp,
                    color    = EventPassColors.TextoSecundario
                )
            }
        }
    }
}

@Composable
private fun ContadorCard(modifier: Modifier, icone: String, valor: String, rotulo: String) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = EventPassColors.Surface), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icone, fontSize = 22.sp)
            Text(valor, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = EventPassColors.TextoPrimario)
            Text(rotulo, fontSize = 12.sp, color = EventPassColors.TextoSecundario)
        }
    }
}
