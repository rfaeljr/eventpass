package br.com.eventpass.portaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.eventpass.portaria.R
import br.com.eventpass.portaria.data.model.ConvidadoResponse
import br.com.eventpass.portaria.ui.theme.EventPassColors
import br.com.eventpass.portaria.viewmodel.PortariaViewModel

@Composable
fun BuscaScreen(
    onResultado: () -> Unit,
    onVoltar: () -> Unit,
    vm: PortariaViewModel = hiltViewModel()
) {
    var texto by remember { mutableStateOf("") }
    val lista by vm.buscaResultados.collectAsState()

    DisposableEffect(Unit) {
        onDispose { vm.limparBusca() }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(EventPassColors.Background)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .background(EventPassColors.Surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVoltar) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.voltar),
                        tint = EventPassColors.TextoPrimario
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    stringResource(R.string.buscar_por_nome),
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = EventPassColors.TextoPrimario
                )
            }
        }

        // ── Campo de busca ────────────────────────────────────────────────────
        Box(Modifier.fillMaxWidth().padding(16.dp)) {
            OutlinedTextField(
                value         = texto,
                onValueChange = {
                    texto = it
                    if (it.length >= 3) vm.buscarPorNome(it)
                    else if (it.isEmpty()) vm.limparBusca()
                },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text(stringResource(R.string.hint_busca_convidado), color = EventPassColors.TextoSecundario) },
                leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null, tint = EventPassColors.TextoSecundario) },
                singleLine    = true,
                shape         = RoundedCornerShape(12.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = EventPassColors.Primario,
                    unfocusedBorderColor = EventPassColors.Divisor,
                    focusedTextColor     = EventPassColors.TextoPrimario,
                    unfocusedTextColor   = EventPassColors.TextoPrimario,
                    cursorColor          = EventPassColors.Primario
                )
            )
        }

        // ── Instrução ─────────────────────────────────────────────────────────
        if (texto.length < 3) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.msg_instrucao_busca),
                        fontSize = 14.sp,
                        color    = EventPassColors.TextoSecundario
                    )
                }
            }
            return@Column
        }

        // ── Resultado vazio ───────────────────────────────────────────────────
        if (lista.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("😕", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.msg_nenhum_convidado), fontSize = 14.sp, color = EventPassColors.TextoSecundario)
                }
            }
            return@Column
        }

        // ── Lista de resultados ───────────────────────────────────────────────
        Text(
            stringResource(R.string.msg_resultados_busca, lista.size),
            fontSize = 12.sp,
            color    = EventPassColors.TextoSecundario,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyColumn(Modifier.fillMaxWidth()) {
            items(lista) { convidado ->
                ConvidadoItem(
                    convidado = convidado,
                    onClick   = {
                        // Registra entrada pelo UUID do convidado usando busca manual
                        vm.registrarEntradaManual(convidado.id.toString(), 0)
                        onResultado()
                    }
                )
            }
        }
    }
}

// ── Item individual da lista ──────────────────────────────────────────────────
@Composable
private fun ConvidadoItem(convidado: ConvidadoResponse, onClick: () -> Unit) {
    val jaEntrou = convidado.statusConvite == "CONFIRMADO"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = EventPassColors.Surface),
        shape  = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    convidado.nome,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = EventPassColors.TextoPrimario
                )
                convidado.grupoTag?.takeIf { it.isNotBlank() }?.let {
                    Text(it, fontSize = 12.sp, color = EventPassColors.TextoSecundario)
                }
                convidado.documento?.let {
                    Text(it, fontSize = 11.sp, color = EventPassColors.TextoSecundario)
                }
            }

            // Badge de status
            Box(
                modifier = Modifier
                    .background(
                        if (jaEntrou) EventPassColors.Verde.copy(alpha = 0.3f)
                        else EventPassColors.Primario.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text     = if (jaEntrou) stringResource(R.string.status_entrou) else stringResource(R.string.status_aguardando),
                    fontSize = 11.sp,
                    color    = if (jaEntrou) EventPassColors.VerdeTexto
                               else EventPassColors.TextoSecundario,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
