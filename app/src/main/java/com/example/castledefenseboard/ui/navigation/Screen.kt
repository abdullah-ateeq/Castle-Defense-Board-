package com.example.castledefenseboard.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Game : Screen("game")
    object Result : Screen("result")
    object HowToPlay : Screen("how_to_play")
    object Settings : Screen("settings")
    object About : Screen("about")
}
