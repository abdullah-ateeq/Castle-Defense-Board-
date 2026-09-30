package com.example.castledefenseboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castledefenseboard.data.model.CastleDefenseState
import com.example.castledefenseboard.ui.components.CastleToken
import com.example.castledefenseboard.ui.theme.CastleGoldPrimary
import com.example.castledefenseboard.ui.theme.CastleHpGreen
import com.example.castledefenseboard.ui.theme.CastleHpRed
import com.example.castledefenseboard.ui.theme.EnergyAmber
import com.example.castledefenseboard.ui.theme.MidnightBackground
import com.example.castledefenseboard.ui.theme.MidnightCard
import com.example.castledefenseboard.ui.theme.StoneBorder

@Composable
fun ResultScreen(
    state: CastleDefenseState,
    onPlayAgainClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val isWin = state.isVictory
    val scrollState = rememberScrollState()

    val formattedDuration = run {
        val totalSeconds = state.durationSeconds
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emblem Banner
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MidnightCard)
                    .border(
                        2.dp,
                        if (isWin) CastleGoldPrimary else CastleHpRed,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CastleToken(castleHp = state.castleHp, maxHp = state.maxCastleHp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = if (isWin) "CASTLE DEFENDED" else "CASTLE FALLEN",
                color = if (isWin) CastleGoldPrimary else CastleHpRed,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isWin) "All 5 waves repelled! The realm stands." else "The walls have breached. Fall back and regroup.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Result Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StoneBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isWin) {
                        ResultRow(label = "Castle HP Remaining", value = "${state.castleHp} / ${state.maxCastleHp}", valueColor = CastleHpGreen)
                        ResultRow(label = "Enemies Defeated", value = "${state.enemiesDefeated}", valueColor = EnergyAmber)
                        ResultRow(label = "Units Deployed", value = "${state.unitsDeployed}")
                        ResultRow(label = "Energy Spent", value = "${state.energySpent}⚡")
                        ResultRow(label = "Turns Survived", value = "${state.turnCount}")
                        ResultRow(label = "Match Duration", value = formattedDuration)
                    } else {
                        ResultRow(label = "Wave Reached", value = "Wave ${state.wave} / ${state.maxWaves}", valueColor = CastleHpRed)
                        ResultRow(label = "Enemies Defeated", value = "${state.enemiesDefeated}", valueColor = EnergyAmber)
                        ResultRow(label = "Turns Survived", value = "${state.turnCount}")
                        ResultRow(label = "Match Duration", value = formattedDuration)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary action button
            Button(
                onClick = onPlayAgainClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CastleGoldPrimary,
                    contentColor = MidnightBackground
                )
            ) {
                Text(
                    text = if (isWin) "PLAY AGAIN" else "TRY AGAIN",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary home button
            OutlinedButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CastleGoldPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StoneBorder))
            ) {
                Text(
                    text = "MAIN MENU",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = Color.White
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
