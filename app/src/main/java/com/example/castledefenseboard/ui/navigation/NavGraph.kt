package com.example.castledefenseboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.castledefenseboard.data.preferences.GameSettings
import com.example.castledefenseboard.ui.screens.AboutScreen
import com.example.castledefenseboard.ui.screens.GameScreen
import com.example.castledefenseboard.ui.screens.HomeScreen
import com.example.castledefenseboard.ui.screens.HowToPlayScreen
import com.example.castledefenseboard.ui.screens.ResultScreen
import com.example.castledefenseboard.ui.screens.SettingsScreen
import com.example.castledefenseboard.ui.screens.SplashScreen
import com.example.castledefenseboard.ui.viewmodel.GameViewModel
import com.example.castledefenseboard.ui.viewmodel.SettingsViewModel

@Composable
fun CastleDefenseNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    gameViewModel: GameViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val gameState by gameViewModel.gameState.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val statistics by settingsViewModel.statistics.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                statistics = statistics,
                onPlayClick = {
                    gameViewModel.startNewGame()
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClick = {
                    navController.navigate(Screen.HowToPlay.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClick = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                state = gameState,
                viewModel = gameViewModel,
                showAttackRangeSetting = settings.showAttackRange,
                onGameOver = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                state = gameState,
                onPlayAgainClick = {
                    gameViewModel.startNewGame()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                onThemeModeChange = { settingsViewModel.setThemeMode(it) },
                onBoardThemeChange = { settingsViewModel.setBoardTheme(it) },
                onSoundToggle = { settingsViewModel.setSoundEnabled(it) },
                onHapticsToggle = { settingsViewModel.setHapticsEnabled(it) },
                onShowRangeToggle = { settingsViewModel.setShowAttackRange(it) },
                onFastAnimationsToggle = { settingsViewModel.setFastAnimations(it) },
                onResetStatistics = { settingsViewModel.resetStatistics() },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
