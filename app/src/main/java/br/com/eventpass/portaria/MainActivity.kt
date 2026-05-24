package br.com.eventpass.portaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import br.com.eventpass.portaria.ui.AppNavigation
import br.com.eventpass.portaria.ui.theme.EventPassColors
import br.com.eventpass.portaria.ui.theme.EventPassTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity do app.
 * @AndroidEntryPoint habilita injeção de dependência do Hilt nesta Activity.
 * Todo o conteúdo visual é gerenciado pelo Jetpack Compose via AppNavigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EventPassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = EventPassColors.Background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
