package br.com.eventpass.portaria

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application do EventPass Portaria.
 * @HiltAndroidApp inicializa o grafo de injeção de dependências do Hilt.
 * Deve ser declarada no AndroidManifest.xml com android:name=".EventPassApp"
 */
@HiltAndroidApp
class EventPassApp : Application()
