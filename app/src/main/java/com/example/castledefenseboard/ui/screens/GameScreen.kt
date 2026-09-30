package com.example.castledefenseboard.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.castledefenseboard.data.model.CastleDefenseState
import com.example.castledefenseboard.data.model.GridPosition
import com.example.castledefenseboard.ui.components.ActionControls
import com.example.castledefenseboard.ui.components.GameHud
import com.example.castledefenseboard.ui.components.RestartConfirmDialog
import com.example.castledefenseboard.ui.components.TacticalBoard
import com.example.castledefenseboard.ui.components.UnitTray
import com.example.castledefenseboard.ui.theme.MidnightBackground
import com.example.castledefenseboard.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    state: CastleDefenseState,
    viewModel: GameViewModel,
    showAttackRangeSetting: Boolean,
    onGameOver: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showRestartDialog by remember { mutableStateOf(false) }

    // When game concludes with Victory or Defeat, briefly display outcome then navigate to Result screen
    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) {
            delay(1200)
            onGameOver()
        }
    }

    if (showRestartDialog) {
        RestartConfirmDialog(
            onConfirm = {
                showRestartDialog = false
                viewModel.startNewGame()
            },
            onDismiss = {
                showRestartDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .padding(12.dp)
    ) {
        if (isLandscape) {
            // Landscape layout: Board left, HUD and controls right
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    TacticalBoard(
                        state = state,
                        onCellClick = { viewModel.onCellClicked(it) },
                        showAttackRangeSetting = showAttackRangeSetting
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    GameHud(
                        state = state,
                        onRestartClick = { showRestartDialog = true }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    UnitTray(
                        selectedType = state.selectedDefenderType,
                        onSelectType = { viewModel.selectDefenderType(it) },
                        currentEnergy = state.energy,
                        currentDefendersCount = state.defenders.size,
                        isActionAvailable = state.isPlayerActionAvailable && !state.isGameOver && !state.isAnimating
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ActionControls(
                        isActionAvailable = state.isPlayerActionAvailable && !state.isGameOver && !state.isAnimating,
                        isUnitSelected = state.selectedDefenderType != null || state.selectedDefenderIdToMove != null,
                        energy = state.energy,
                        onSkipClick = { viewModel.skipTurn() },
                        onCancelSelection = { viewModel.cancelSelection() }
                    )
                }
            }
        } else {
            // Portrait layout: HUD top, Board center, Tray & Actions bottom
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                GameHud(
                    state = state,
                    onRestartClick = { showRestartDialog = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                TacticalBoard(
                    state = state,
                    onCellClick = { viewModel.onCellClicked(it) },
                    showAttackRangeSetting = showAttackRangeSetting,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                UnitTray(
                    selectedType = state.selectedDefenderType,
                    onSelectType = { viewModel.selectDefenderType(it) },
                    currentEnergy = state.energy,
                    currentDefendersCount = state.defenders.size,
                    isActionAvailable = state.isPlayerActionAvailable && !state.isGameOver && !state.isAnimating
                )

                Spacer(modifier = Modifier.height(10.dp))

                ActionControls(
                    isActionAvailable = state.isPlayerActionAvailable && !state.isGameOver && !state.isAnimating,
                    isUnitSelected = state.selectedDefenderType != null || state.selectedDefenderIdToMove != null,
                    energy = state.energy,
                    onSkipClick = { viewModel.skipTurn() },
                    onCancelSelection = { viewModel.cancelSelection() }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
