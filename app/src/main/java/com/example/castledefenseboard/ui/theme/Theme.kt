package com.example.castledefenseboard.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.castledefenseboard.data.preferences.BoardThemeStyle
import com.example.castledefenseboard.data.preferences.ThemeMode

val LocalBoardThemeStyle = staticCompositionLocalOf { BoardThemeStyle.STONE }

private val TacticalDarkColorScheme = darkColorScheme(
    primary = CastleGoldPrimary,
    onPrimary = MidnightBackground,
    primaryContainer = CastleStone,
    onPrimaryContainer = CastleGoldPrimary,
    secondary = GuardSteelBlue,
    onSecondary = DefenderIvory,
    secondaryContainer = MidnightElevated,
    onSecondaryContainer = DefenderIvory,
    tertiary = RaiderCrimson,
    onTertiary = DefenderIvory,
    background = MidnightBackground,
    onBackground = TextPrimaryDark,
    surface = MidnightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = MidnightCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = StoneBorder
)

private val TacticalLightColorScheme = lightColorScheme(
    primary = CastleGoldSecondary,
    onPrimary = ParchmentBackground,
    primaryContainer = ParchmentCard,
    onPrimaryContainer = CastleGoldSecondary,
    secondary = GuardSteelBlue,
    onSecondary = ParchmentBackground,
    secondaryContainer = ParchmentSurface,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = RaiderCrimson,
    onTertiary = ParchmentBackground,
    background = ParchmentBackground,
    onBackground = TextPrimaryLight,
    surface = ParchmentSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = ParchmentCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = ParchmentBorder
)

@Composable
fun CastleDefenseBoardTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    boardTheme: BoardThemeStyle = BoardThemeStyle.STONE,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) TacticalDarkColorScheme else TacticalLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalBoardThemeStyle provides boardTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}