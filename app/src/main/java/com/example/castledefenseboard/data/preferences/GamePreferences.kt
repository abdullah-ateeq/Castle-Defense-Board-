package com.example.castledefenseboard.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "castle_defense_prefs")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class BoardThemeStyle {
    STONE,
    MIDNIGHT
}

data class GameSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val boardTheme: BoardThemeStyle = BoardThemeStyle.STONE,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showAttackRange: Boolean = true,
    val fastAnimations: Boolean = false
)

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val defeats: Int = 0,
    val bestCastleHp: Int = 0,
    val mostEnemiesDefeated: Int = 0
)

class GamePreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SHOW_ATTACK_RANGE = booleanPreferencesKey("show_attack_range")
        val FAST_ANIMATIONS = booleanPreferencesKey("fast_animations")

        val GAMES_PLAYED = intPreferencesKey("games_played")
        val WINS = intPreferencesKey("wins")
        val DEFEATS = intPreferencesKey("defeats")
        val BEST_CASTLE_HP = intPreferencesKey("best_castle_hp")
        val MOST_ENEMIES_DEFEATED = intPreferencesKey("most_enemies_defeated")
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        val themeString = prefs[PreferencesKeys.THEME_MODE] ?: ThemeMode.DARK.name
        val boardThemeString = prefs[PreferencesKeys.BOARD_THEME] ?: BoardThemeStyle.STONE.name

        GameSettings(
            themeMode = runCatching { ThemeMode.valueOf(themeString) }.getOrDefault(ThemeMode.DARK),
            boardTheme = runCatching { BoardThemeStyle.valueOf(boardThemeString) }.getOrDefault(BoardThemeStyle.STONE),
            soundEnabled = prefs[PreferencesKeys.SOUND_ENABLED] ?: true,
            hapticsEnabled = prefs[PreferencesKeys.HAPTICS_ENABLED] ?: true,
            showAttackRange = prefs[PreferencesKeys.SHOW_ATTACK_RANGE] ?: true,
            fastAnimations = prefs[PreferencesKeys.FAST_ANIMATIONS] ?: false
        )
    }

    val statisticsFlow: Flow<GameStatistics> = context.dataStore.data.map { prefs ->
        GameStatistics(
            gamesPlayed = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0,
            wins = prefs[PreferencesKeys.WINS] ?: 0,
            defeats = prefs[PreferencesKeys.DEFEATS] ?: 0,
            bestCastleHp = prefs[PreferencesKeys.BEST_CASTLE_HP] ?: 0,
            mostEnemiesDefeated = prefs[PreferencesKeys.MOST_ENEMIES_DEFEATED] ?: 0
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode.name }
    }

    suspend fun setBoardTheme(theme: BoardThemeStyle) {
        context.dataStore.edit { it[PreferencesKeys.BOARD_THEME] = theme.name }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setShowAttackRange(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_ATTACK_RANGE] = show }
    }

    suspend fun setFastAnimations(fast: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.FAST_ANIMATIONS] = fast }
    }

    suspend fun recordGameResult(isVictory: Boolean, castleHp: Int, enemiesDefeated: Int) {
        context.dataStore.edit { prefs ->
            val gamesPlayed = (prefs[PreferencesKeys.GAMES_PLAYED] ?: 0) + 1
            prefs[PreferencesKeys.GAMES_PLAYED] = gamesPlayed

            if (isVictory) {
                val wins = (prefs[PreferencesKeys.WINS] ?: 0) + 1
                prefs[PreferencesKeys.WINS] = wins
            } else {
                val defeats = (prefs[PreferencesKeys.DEFEATS] ?: 0) + 1
                prefs[PreferencesKeys.DEFEATS] = defeats
            }

            val currentBestHp = prefs[PreferencesKeys.BEST_CASTLE_HP] ?: 0
            if (isVictory && castleHp > currentBestHp) {
                prefs[PreferencesKeys.BEST_CASTLE_HP] = castleHp
            }

            val currentMostDefeated = prefs[PreferencesKeys.MOST_ENEMIES_DEFEATED] ?: 0
            if (enemiesDefeated > currentMostDefeated) {
                prefs[PreferencesKeys.MOST_ENEMIES_DEFEATED] = enemiesDefeated
            }
        }
    }

    suspend fun resetStatistics() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.GAMES_PLAYED] = 0
            prefs[PreferencesKeys.WINS] = 0
            prefs[PreferencesKeys.DEFEATS] = 0
            prefs[PreferencesKeys.BEST_CASTLE_HP] = 0
            prefs[PreferencesKeys.MOST_ENEMIES_DEFEATED] = 0
        }
    }
}
