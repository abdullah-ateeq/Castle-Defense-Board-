package com.example.castledefenseboard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castledefenseboard.data.model.DefenderType
import com.example.castledefenseboard.data.model.DefenderUnit
import com.example.castledefenseboard.data.model.EnemyType
import com.example.castledefenseboard.data.model.EnemyUnit
import com.example.castledefenseboard.data.model.GridPosition
import com.example.castledefenseboard.ui.components.CastleToken
import com.example.castledefenseboard.ui.components.DefenderToken
import com.example.castledefenseboard.ui.components.EnemyToken
import com.example.castledefenseboard.ui.theme.AttackLineBeam
import com.example.castledefenseboard.ui.theme.BoardGridLine
import com.example.castledefenseboard.ui.theme.CastleGoldPrimary
import com.example.castledefenseboard.ui.theme.GuardSteelBlue
import com.example.castledefenseboard.ui.theme.MidnightBackground
import com.example.castledefenseboard.ui.theme.MidnightCard
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation progress stages
    var stage by remember { mutableStateOf(0) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Stage 1: Grid fades in
        delay(250)
        stage = 1
        // Stage 2: Castle appears
        delay(300)
        stage = 2
        // Stage 3: Defender deploys
        delay(250)
        stage = 3
        // Stage 4: Enemy advances
        delay(250)
        stage = 4
        // Stage 5: Unit fires
        delay(200)
        stage = 5
        // Stage 6: Enemy defeated, Title appears
        delay(200)
        stage = 6
        titleAlpha.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        // Delay and finish
        delay(350)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Miniature Battle Board
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightCard)
                    .border(2.dp, CastleGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background grid lines (3x3 mini grid)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = size.width / 3f
                    for (i in 1..2) {
                        drawLine(
                            color = BoardGridLine.copy(alpha = 0.6f),
                            start = Offset(i * step, 0f),
                            end = Offset(i * step, size.height),
                            strokeWidth = 1.5f
                        )
                        drawLine(
                            color = BoardGridLine.copy(alpha = 0.6f),
                            start = Offset(0f, i * step),
                            end = Offset(size.width, i * step),
                            strokeWidth = 1.5f
                        )
                    }

                    // Beam when firing
                    if (stage == 5) {
                        val start = Offset(size.width * 0.5f, size.height * 0.5f)
                        val end = Offset(size.width * 0.5f, size.height * 0.16f)
                        drawLine(
                            color = GuardSteelBlue.copy(alpha = 0.5f),
                            start = start,
                            end = end,
                            strokeWidth = 12f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = AttackLineBeam,
                            start = start,
                            end = end,
                            strokeWidth = 4f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Castle at bottom row (row 2, col 1 in 3x3)
                if (stage >= 2) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        CastleToken(castleHp = 10, maxHp = 10)
                    }
                }

                // Defender at center (row 1, col 1 in 3x3)
                if (stage >= 3) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .align(Alignment.Center)
                    ) {
                        DefenderToken(
                            defender = DefenderUnit(
                                id = 1,
                                type = DefenderType.GUARD,
                                position = GridPosition(1, 1)
                            )
                        )
                    }
                }

                // Enemy at top (row 0, col 1 in 3x3), disappears in stage 6
                if (stage in 4..5) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        EnemyToken(
                            enemy = EnemyUnit(
                                id = 1,
                                type = EnemyType.RAIDER,
                                position = GridPosition(0, 1),
                                hp = 2,
                                maxHp = 2
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(titleAlpha.value)
            ) {
                Text(
                    text = "CASTLE DEFENSE BOARD",
                    color = CastleGoldPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Deploy wisely. Hold the line.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
