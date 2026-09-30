package com.example.castledefenseboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.castledefenseboard.ui.navigation.CastleDefenseNavGraph
import com.example.castledefenseboard.ui.theme.CastleDefenseBoardTheme
import com.example.castledefenseboard.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()

            CastleDefenseBoardTheme(
                themeMode = settings.themeMode,
                boardTheme = settings.boardTheme
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CastleDefenseNavGraph(
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}