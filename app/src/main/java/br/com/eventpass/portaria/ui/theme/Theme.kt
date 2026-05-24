package br.com.eventpass.portaria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object EventPassColors {
    val Background      = Color(0xFF0B0F1A)
    val Surface         = Color(0xFF131929)
    val SurfaceCard     = Color(0xFF1A2235)
    val Divisor         = Color(0xFF1E293B)

    val Verde           = Color(0xFF1B5E20)
    val VerdeClear      = Color(0xFF2E7D32)
    val VerdeTexto      = Color(0xFF81C784)

    val Amarelo         = Color(0xFFF57F17)
    val AmareloClear    = Color(0xFFF9A825)
    val AmareloTexto    = Color(0xFFFFEB3B)

    val Vermelho        = Color(0xFFB71C1C)
    val VermelhoClear   = Color(0xFFC62828)
    val VermelhoTexto   = Color(0xFFEF9A9A)

    val Primario        = Color(0xFF7C3AED)
    val PrimarioClear   = Color(0xFF9333EA)

    val TextoPrimario   = Color(0xFFE2E8F0)
    val TextoSecundario = Color(0xFF94A3B8)
    val Branco          = Color(0xFFFFFFFF)
}

private val DarkColorScheme = darkColorScheme(
    primary     = EventPassColors.Primario,
    background  = EventPassColors.Background,
    surface     = EventPassColors.Surface,
    onPrimary   = EventPassColors.Branco,
    onBackground = EventPassColors.TextoPrimario,
    onSurface   = EventPassColors.TextoPrimario
)

@Composable
fun EventPassTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
}
