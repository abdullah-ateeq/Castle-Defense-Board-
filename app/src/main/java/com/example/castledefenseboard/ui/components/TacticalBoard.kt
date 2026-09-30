package com.example.castledefenseboard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castledefenseboard.data.model.AttackEvent
import com.example.castledefenseboard.data.model.CastleDamageEvent
import com.example.castledefenseboard.data.model.CastleDefenseState
import com.example.castledefenseboard.data.model.DefenderType
import com.example.castledefenseboard.data.model.DefenderUnit
import com.example.castledefenseboard.data.model.EnemyUnit
import com.example.castledefenseboard.data.model.GridPosition
import com.example.castledefenseboard.data.preferences.BoardThemeStyle
import com.example.castledefenseboard.domain.GameRules
import com.example.castledefenseboard.ui.theme.ArcherCobalt
import com.example.castledefenseboard.ui.theme.AttackLineBeam
import com.example.castledefenseboard.ui.theme.AttackRangeRed
import com.example.castledefenseboard.ui.theme.BoardGridLine
import com.example.castledefenseboard.ui.theme.BoardMidnightCellDark
import com.example.castledefenseboard.ui.theme.BoardMidnightCellLight
import com.example.castledefenseboard.ui.theme.BoardStoneCellDark
import com.example.castledefenseboard.ui.theme.BoardStoneCellLight
import com.example.castledefenseboard.ui.theme.CastleGoldPrimary
import com.example.castledefenseboard.ui.theme.CastleHpRed
import com.example.castledefenseboard.ui.theme.GuardSteelBlue
import com.example.castledefenseboard.ui.theme.LegalMoveGreen
import com.example.castledefenseboard.ui.theme.LocalBoardThemeStyle
import com.example.castledefenseboard.ui.theme.MageRuneViolet
import com.example.castledefenseboard.ui.theme.SelectionCyan
import kotlin.math.roundToInt

@Composable
fun TacticalBoard(
    state: CastleDefenseState,
    onCellClick: (GridPosition) -> Unit,
    showAttackRangeSetting: Boolean,
    modifier: Modifier = Modifier
) {
    val boardTheme = LocalBoardThemeStyle.current

    // Determine cell colors based on theme
    val cellDark = if (boardTheme == BoardThemeStyle.STONE) BoardStoneCellDark else BoardMidnightCellDark
    val cellLight = if (boardTheme == BoardThemeStyle.STONE) BoardStoneCellLight else BoardMidnightCellLight

    // Determine positions to highlight
    val selectedDefender = state.defenders.find { it.id == state.selectedDefenderIdToMove }

    val validPlacementCells = remember(state.selectedDefenderType, state.defenders) {
        if (state.selectedDefenderType != null && state.isPlayerActionAvailable) {
            GameRules.getValidPlacementPositions(state.defenders).toSet()
        } else {
            emptySet()
        }
    }

    val validMoveCells = remember(selectedDefender, state.defenders) {
        if (selectedDefender != null && state.isPlayerActionAvailable && state.energy >= GameRules.MOVE_COST) {
            GameRules.getValidMoveDestinations(selectedDefender, state.defenders).toSet()
        } else {
            emptySet()
        }
    }

    val rangePreviewCells = remember(selectedDefender, showAttackRangeSetting) {
        if (showAttackRangeSetting && selectedDefender != null) {
            GameRules.getAttackRangeCells(selectedDefender).toSet()
        } else {
            emptySet()
        }
    }

    // Pulse animation for active highlights
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(cellDark)
            .border(2.dp, CastleGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val cellSizePx = boardWidthPx / GameRules.GRID_COLS
        val density = LocalDensity.current

        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until GameRules.GRID_ROWS) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    for (col in 0 until GameRules.GRID_COLS) {
                        val pos = GridPosition(row, col)
                        val isEven = (row + col) % 2 == 0
                        val baseColor = if (isEven) cellLight else cellDark

                        val isLegalZone = row in GameRules.LEGAL_DEFENSE_ROWS
                        val isEntryCol = row == 0 && col in GameRules.ENTRY_COLUMNS
                        val isCastle = pos == GameRules.CASTLE_POSITION
                        val isPlacementLegal = pos in validPlacementCells
                        val isMoveLegal = pos in validMoveCells
                        val isInRange = pos in rangePreviewCells

                        val defenderHere = state.defenders.find { it.position == pos }
                        val enemyHere = state.enemies.find { it.position == pos && it.hp > 0 }
                        val isSelectedDefender = defenderHere != null && defenderHere.id == state.selectedDefenderIdToMove

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(baseColor)
                                .border(0.5.dp, BoardGridLine.copy(alpha = 0.4f))
                                .then(
                                    if (isPlacementLegal) {
                                        Modifier
                                            .border(
                                                width = 2.dp,
                                                color = CastleGoldPrimary.copy(alpha = pulseAlpha),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .background(CastleGoldPrimary.copy(alpha = 0.12f))
                                    } else if (isMoveLegal) {
                                        Modifier
                                            .border(
                                                width = 2.dp,
                                                color = LegalMoveGreen.copy(alpha = pulseAlpha),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .background(LegalMoveGreen.copy(alpha = 0.14f))
                                    } else if (isInRange) {
                                        Modifier.background(AttackRangeRed.copy(alpha = 0.14f))
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    role = Role.Button,
                                    enabled = !state.isGameOver && !state.isAnimating
                                ) {
                                    onCellClick(pos)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Subtle background hints for entry and defense zone
                            if (isEntryCol && enemyHere == null) {
                                Text(
                                    text = "▼",
                                    color = Color(0xFFC54545).copy(alpha = 0.35f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (isLegalZone && defenderHere == null && !isPlacementLegal && !isMoveLegal) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                )
                            }

                            // Render Unit Tokens
                            if (isCastle) {
                                CastleToken(castleHp = state.castleHp, maxHp = state.maxCastleHp)
                            } else if (defenderHere != null) {
                                DefenderToken(
                                    defender = defenderHere,
                                    isSelected = isSelectedDefender
                                )
                            } else if (enemyHere != null) {
                                EnemyToken(enemy = enemyHere)
                            }
                        }
                    }
                }
            }
        }

        // Overlay 1: Attack Beams & Projectile lines
        if (state.activeAttackEvents.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                state.activeAttackEvents.forEach { attack ->
                    val startX = (attack.defenderPos.col + 0.5f) * cellSizePx
                    val startY = (attack.defenderPos.row + 0.5f) * cellSizePx
                    val endX = (attack.targetPos.col + 0.5f) * cellSizePx
                    val endY = (attack.targetPos.row + 0.5f) * cellSizePx

                    val beamColor = when (attack.type) {
                        DefenderType.GUARD -> GuardSteelBlue
                        DefenderType.ARCHER -> ArcherCobalt
                        DefenderType.MAGE -> MageRuneViolet
                    }

                    // Outer laser halo
                    drawLine(
                        color = beamColor.copy(alpha = 0.4f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                    // Core attack beam
                    drawLine(
                        color = AttackLineBeam,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 3.5f,
                        cap = StrokeCap.Round
                    )
                    // Impact explosion circle
                    drawCircle(
                        color = AttackLineBeam,
                        radius = 9f,
                        center = Offset(endX, endY)
                    )
                }
            }
        }

        // Overlay 2: Floating Damage Indicators for Attacks
        state.activeAttackEvents.forEach { attack ->
            val col = attack.targetPos.col
            val row = attack.targetPos.row
            val offsetX = (col * cellSizePx) + (cellSizePx * 0.25f)
            val offsetY = (row * cellSizePx) - (cellSizePx * 0.2f)

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                    }
                    .background(Color(0xD9000000), RoundedCornerShape(4.dp))
                    .border(1.dp, AttackLineBeam, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "-${attack.damage}",
                    color = AttackLineBeam,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Overlay 3: Castle Damage Alert
        state.activeCastleDamage?.let { damageEvent ->
            val castleCol = damageEvent.col
            val offsetX = (castleCol * cellSizePx) + (cellSizePx * 0.1f)
            val offsetY = (5 * cellSizePx) - (cellSizePx * 0.35f)

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                    }
                    .background(Color(0xE67F1D1D), RoundedCornerShape(6.dp))
                    .border(1.5.dp, CastleHpRed, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "CASTLE -${damageEvent.damage}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
