package com.example.castledefenseboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castledefenseboard.data.model.CastleDefenseState
import com.example.castledefenseboard.data.model.DefenderType
import com.example.castledefenseboard.domain.GameRules
import com.example.castledefenseboard.ui.theme.ArcherCobalt
import com.example.castledefenseboard.ui.theme.CastleGoldPrimary
import com.example.castledefenseboard.ui.theme.CastleHpGreen
import com.example.castledefenseboard.ui.theme.CastleHpOrange
import com.example.castledefenseboard.ui.theme.CastleHpRed
import com.example.castledefenseboard.ui.theme.EnergyAmber
import com.example.castledefenseboard.ui.theme.GuardSteelBlue
import com.example.castledefenseboard.ui.theme.MageRuneViolet
import com.example.castledefenseboard.ui.theme.MidnightCard
import com.example.castledefenseboard.ui.theme.MidnightElevated
import com.example.castledefenseboard.ui.theme.StoneBorder

@Composable
fun GameHud(
    state: CastleDefenseState,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StoneBorder))
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wave Counter
                HudMetric(
                    label = "WAVE",
                    value = "${state.wave} / ${state.maxWaves}",
                    accentColor = CastleGoldPrimary,
                    contentDesc = "Wave ${state.wave} of ${state.maxWaves}"
                )

                // Castle HP
                val hpColor = when {
                    state.castleHp >= 7 -> CastleHpGreen
                    state.castleHp >= 4 -> CastleHpOrange
                    else -> CastleHpRed
                }
                HudMetric(
                    label = "CASTLE",
                    value = "${state.castleHp} / ${state.maxCastleHp}",
                    accentColor = hpColor,
                    contentDesc = "Castle health ${state.castleHp} of ${state.maxCastleHp}"
                )

                // Energy
                HudMetric(
                    label = "ENERGY",
                    value = "${state.energy} / ${state.maxEnergy}",
                    accentColor = EnergyAmber,
                    contentDesc = "Energy ${state.energy} of ${state.maxEnergy}"
                )

                // Active Enemies
                val totalEnemies = state.enemies.count { it.hp > 0 } + state.spawnQueue.size
                HudMetric(
                    label = "ENEMIES",
                    value = "$totalEnemies",
                    accentColor = Color(0xFFEF4444),
                    contentDesc = "$totalEnemies enemies remaining"
                )

                IconButton(
                    onClick = onRestartClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Game",
                        tint = CastleGoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-status line: turn counter & current action guide
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.statusMessage,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = "Turn ${state.turnCount}",
                    color = CastleGoldPrimary.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HudMetric(
    label: String,
    value: String,
    accentColor: Color,
    contentDesc: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics { contentDescription = contentDesc }
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accentColor
        )
    }
}

@Composable
fun UnitTray(
    selectedType: DefenderType?,
    onSelectType: (DefenderType) -> Unit,
    currentEnergy: Int,
    currentDefendersCount: Int,
    isActionAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StoneBorder))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DEPLOY DEFENDER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CastleGoldPrimary,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Units: $currentDefendersCount/${GameRules.MAX_DEFENDERS}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (currentDefendersCount >= GameRules.MAX_DEFENDERS) CastleHpOrange else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefenderType.values().forEach { type ->
                    val canAfford = currentEnergy >= type.cost
                    val isLimitReached = currentDefendersCount >= GameRules.MAX_DEFENDERS
                    val isEnabled = isActionAvailable && canAfford && !isLimitReached
                    val isSelected = selectedType == type

                    UnitTrayCard(
                        type = type,
                        isSelected = isSelected,
                        isEnabled = isEnabled,
                        canAfford = canAfford,
                        onClick = { onSelectType(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitTrayCard(
    type: DefenderType,
    isSelected: Boolean,
    isEnabled: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = when (type) {
        DefenderType.GUARD -> GuardSteelBlue
        DefenderType.ARCHER -> ArcherCobalt
        DefenderType.MAGE -> MageRuneViolet
    }

    val borderColor = when {
        isSelected -> CastleGoldPrimary
        isEnabled -> StoneBorder
        else -> StoneBorder.copy(alpha = 0.4f)
    }

    val backgroundColor = when {
        isSelected -> MidnightElevated
        isEnabled -> MidnightCard
        else -> MidnightCard.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .semantics { contentDescription = "${type.title} costs ${type.cost} energy. ${type.rangeDescription}. Deals ${type.damage} damage." }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = type.title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isEnabled) typeColor else typeColor.copy(alpha = 0.4f),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "⚡ ${type.cost}",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (canAfford) EnergyAmber else EnergyAmber.copy(alpha = 0.35f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dmg ${type.damage}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionControls(
    isActionAvailable: Boolean,
    isUnitSelected: Boolean,
    energy: Int,
    onSkipClick: () -> Unit,
    onCancelSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isUnitSelected) {
            OutlinedButton(
                onClick = onCancelSelection,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StoneBorder))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Selection",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CANCEL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = onSkipClick,
            enabled = isActionAvailable,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MidnightElevated,
                contentColor = CastleGoldPrimary,
                disabledContainerColor = MidnightElevated.copy(alpha = 0.4f),
                disabledContentColor = CastleGoldPrimary.copy(alpha = 0.4f)
            ),
            border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CastleGoldPrimary.copy(alpha = 0.6f)))
        ) {
            Icon(
                imageVector = Icons.Default.FastForward,
                contentDescription = "Skip Turn",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SKIP TURN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}
