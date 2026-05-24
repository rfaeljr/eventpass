package br.com.eventpass.portaria.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.eventpass.portaria.ui.screens.*

sealed class Tela(val rota: String) {
    object Splash      : Tela("splash")
    object Pareamento  : Tela("pareamento")
    object Confirmacao : Tela("confirmacao")
    object Home        : Tela("home")
    object Scanner     : Tela("scanner")
    object Resultado   : Tela("resultado")
    object Busca       : Tela("busca")
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()

    NavHost(nav, startDestination = Tela.Splash.rota) {

        composable(Tela.Splash.rota) {
            SplashScreen(
                onTemToken = { nav.navigate(Tela.Home.rota) { popUpTo(Tela.Splash.rota) { inclusive = true } } },
                onSemToken = { nav.navigate(Tela.Pareamento.rota) { popUpTo(Tela.Splash.rota) { inclusive = true } } }
            )
        }

        composable(Tela.Pareamento.rota) {
            PareamentoScreen(
                onPareado = { nav.navigate(Tela.Confirmacao.rota) { popUpTo(Tela.Pareamento.rota) { inclusive = true } } }
            )
        }

        composable(Tela.Confirmacao.rota) {
            ConfirmacaoScreen(
                onContinuar = { nav.navigate(Tela.Home.rota) { popUpTo(Tela.Confirmacao.rota) { inclusive = true } } }
            )
        }

        composable(Tela.Home.rota) {
            HomeScreen(
                onIniciarLeitura = { nav.navigate(Tela.Scanner.rota) },
                onBuscarPorNome  = { nav.navigate(Tela.Busca.rota) }
            )
        }

        composable(Tela.Scanner.rota) {
            ScannerScreen(
                onResultado = { nav.navigate(Tela.Resultado.rota) },
                onVoltar    = { nav.popBackStack() }
            )
        }

        composable(Tela.Resultado.rota) {
            ResultadoScreen(
                onProximo = { nav.navigate(Tela.Scanner.rota) { popUpTo(Tela.Scanner.rota) { inclusive = true } } }
            )
        }

        composable(Tela.Busca.rota) {
            BuscaScreen(
                onResultado = { nav.navigate(Tela.Resultado.rota) },
                onVoltar    = { nav.popBackStack() }
            )
        }
    }
}
