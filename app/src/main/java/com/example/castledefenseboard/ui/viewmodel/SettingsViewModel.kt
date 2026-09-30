package com.example.castledefenseboard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.castledefenseboard.data.preferences.BoardThemeStyle
import com.example.castledefenseboard.data.preferences.GamePreferencesRepository
import com.example.castledefenseboard.data.preferences.GameSettings
import com.example.castledefenseboard.data.preferences.GameStatistics
import com.example.castledefenseboard.data.preferences.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GamePreferencesRepository(application)

    val settings: StateFlow<GameSettings> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameSettings()
        )

    val statistics: StateFlow<GameStatistics> = repository.statisticsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameStatistics()
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setBoardTheme(theme: BoardThemeStyle) {
        viewModelScope.launch { repository.setBoardTheme(theme) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundEnabled(enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setHapticsEnabled(enabled) }
    }

    fun setShowAttackRange(show: Boolean) {
        viewModelScope.launch { repository.setShowAttackRange(show) }
    }

    fun setFastAnimations(fast: Boolean) {
        viewModelScope.launch { repository.setFastAnimations(fast) }
    }

    fun resetStatistics() {
        viewModelScope.launch { repository.resetStatistics() }
    }
}
