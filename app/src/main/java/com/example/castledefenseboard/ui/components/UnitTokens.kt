package com.example.castledefenseboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castledefenseboard.data.model.DefenderType
import com.example.castledefenseboard.data.model.DefenderUnit
import com.example.castledefenseboard.data.model.EnemyType
import com.example.castledefenseboard.data.model.EnemyUnit
import com.example.castledefenseboard.ui.theme.ArcherCobalt
import com.example.castledefenseboard.ui.theme.BruteBloodstone
import com.example.castledefenseboard.ui.theme.CastleGoldPrimary
import com.example.castledefenseboard.ui.theme.CastleGoldSecondary
import com.example.castledefenseboard.ui.theme.CastleStone
import com.example.castledefenseboard.ui.theme.DefenderIvory
import com.example.castledefenseboard.ui.theme.GuardSteelBlue
import com.example.castledefenseboard.ui.theme.MageRuneViolet
import com.example.castledefenseboard.ui.theme.MidnightBackground
import com.example.castledefenseboard.ui.theme.RaiderCrimson
import com.example.castledefenseboard.ui.theme.ScoutRust

@Composable
fun DefenderToken(
    defender: DefenderUnit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val description = "${defender.type.title} at row ${defender.position.row + 1}, column ${defender.position.col + 1}"
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (defender.type) {
                DefenderType.GUARD -> drawGuardShield(isSelected)
                DefenderType.ARCHER -> drawArcherToken(isSelected)
                DefenderType.MAGE -> drawMageToken(isSelected)
            }
        }
    }
}

@Composable
fun EnemyToken(
    enemy: EnemyUnit,
    modifier: Modifier = Modifier
) {
    val description = "${enemy.type.title} with ${enemy.hp} health at row ${enemy.position.row + 1}, column ${enemy.position.col + 1}"
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (enemy.type) {
                EnemyType.RAIDER -> drawRaiderToken()
                EnemyType.SCOUT -> drawScoutToken()
                EnemyType.BRUTE -> drawBruteToken()
            }
            // Draw HP indicator pips at top of cell
            drawHpPips(enemy.hp, enemy.maxHp)
        }
    }
}

@Composable
fun CastleToken(
    castleHp: Int,
    maxHp: Int = 10,
    modifier: Modifier = Modifier
) {
    val description = "Castle base, health $castleHp of $maxHp"
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(3.dp)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCastleBase()
        }
        Text(
            text = "$castleHp",
            color = CastleGoldPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// -------------------------------------------------------------
// DEFENDER TOKEN DRAWINGS
// -------------------------------------------------------------

private fun DrawScope.drawGuardShield(isSelected: Boolean) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    val shieldPath = Path().apply {
        moveTo(w * 0.22f, h * 0.20f)
        lineTo(w * 0.78f, h * 0.20f)
        lineTo(w * 0.80f, h * 0.55f)
        quadraticTo(w * 0.75f, h * 0.84f, cx, h * 0.90f)
        quadraticTo(w * 0.25f, h * 0.84f, w * 0.20f, h * 0.55f)
        close()
    }

    // Outer glow / selection outline
    if (isSelected) {
        drawPath(
            path = shieldPath,
            color = CastleGoldPrimary,
            style = Stroke(width = 6f)
        )
    }

    // Background gradient fill
    drawPath(
        path = shieldPath,
        brush = Brush.verticalGradient(
            colors = listOf(GuardSteelBlue, Color(0xFF385568)),
            startY = h * 0.2f,
            endY = h * 0.9f
        ),
        style = Fill
    )

    // Silver/Ivory rim
    drawPath(
        path = shieldPath,
        color = DefenderIvory.copy(alpha = 0.85f),
        style = Stroke(width = 2.5f)
    )

    // Shield Emblem: Central Cross
    val crossPath = Path().apply {
        moveTo(cx - 3f, h * 0.32f)
        lineTo(cx + 3f, h * 0.32f)
        lineTo(cx + 3f, h * 0.76f)
        lineTo(cx - 3f, h * 0.76f)
        close()

        moveTo(w * 0.35f, cy - 3f)
        lineTo(w * 0.65f, cy - 3f)
        lineTo(w * 0.65f, cy + 3f)
        lineTo(w * 0.35f, cy + 3f)
        close()
    }
    drawPath(path = crossPath, color = DefenderIvory, style = Fill)
}

private fun DrawScope.drawArcherToken(isSelected: Boolean) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    // Outer circular disc
    val radius = size.minDimension * 0.38f
    if (isSelected) {
        drawCircle(
            color = CastleGoldPrimary,
            radius = radius + 3f,
            center = Offset(cx, cy),
            style = Stroke(width = 4f)
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(ArcherCobalt, Color(0xFF2C646B)),
            center = Offset(cx, cy),
            radius = radius
        ),
        radius = radius,
        center = Offset(cx, cy)
    )

    drawCircle(
        color = DefenderIvory.copy(alpha = 0.8f),
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = 2.5f)
    )

    // Arrow / Bow Motif pointing upward
    val arrowPath = Path().apply {
        // Arrowhead
        moveTo(cx, cy - radius * 0.65f)
        lineTo(cx - radius * 0.38f, cy - radius * 0.15f)
        lineTo(cx - radius * 0.12f, cy - radius * 0.15f)
        lineTo(cx - radius * 0.12f, cy + radius * 0.55f)
        lineTo(cx + radius * 0.12f, cy + radius * 0.55f)
        lineTo(cx + radius * 0.12f, cy - radius * 0.15f)
        lineTo(cx + radius * 0.38f, cy - radius * 0.15f)
        close()
    }
    drawPath(path = arrowPath, color = DefenderIvory, style = Fill)
}

private fun DrawScope.drawMageToken(isSelected: Boolean) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    val diamondPath = Path().apply {
        moveTo(cx, h * 0.16f)
        lineTo(w * 0.84f, cy)
        lineTo(cx, h * 0.84f)
        lineTo(w * 0.16f, cy)
        close()
    }

    if (isSelected) {
        drawPath(
            path = diamondPath,
            color = CastleGoldPrimary,
            style = Stroke(width = 5f)
        )
    }

    drawPath(
        path = diamondPath,
        brush = Brush.linearGradient(
            colors = listOf(MageRuneViolet, Color(0xFF4C3B91)),
            start = Offset(w * 0.2f, h * 0.2f),
            end = Offset(w * 0.8f, h * 0.8f)
        ),
        style = Fill
    )

    drawPath(
        path = diamondPath,
        color = DefenderIvory.copy(alpha = 0.85f),
        style = Stroke(width = 2.5f)
    )

    // 4-pointed Arcane Star Core
    val starPath = Path().apply {
        moveTo(cx, cy - h * 0.18f)
        quadraticTo(cx, cy, cx + w * 0.18f, cy)
        quadraticTo(cx, cy, cx, cy + h * 0.18f)
        quadraticTo(cx, cy, cx - w * 0.18f, cy)
        quadraticTo(cx, cy, cx, cy - h * 0.18f)
        close()
    }
    drawPath(path = starPath, color = DefenderIvory, style = Fill)
}

// -------------------------------------------------------------
// ENEMY TOKEN DRAWINGS
// -------------------------------------------------------------

private fun DrawScope.drawRaiderToken() {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    // Angular downward chevron / wedge token
    val path = Path().apply {
        moveTo(w * 0.22f, h * 0.32f)
        lineTo(w * 0.78f, h * 0.32f)
        lineTo(w * 0.72f, h * 0.62f)
        lineTo(cx, h * 0.88f)
        lineTo(w * 0.28f, h * 0.62f)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(RaiderCrimson, Color(0xFF7A2020)),
            startY = h * 0.3f,
            endY = h * 0.9f
        ),
        style = Fill
    )

    drawPath(
        path = path,
        color = Color(0xFFFFB3B3).copy(alpha = 0.7f),
        style = Stroke(width = 2f)
    )

    // Inner blade accent
    val accentPath = Path().apply {
        moveTo(cx, h * 0.42f)
        lineTo(cx, h * 0.75f)
    }
    drawPath(accentPath, color = DefenderIvory, style = Stroke(width = 2.5f))
}

private fun DrawScope.drawScoutToken() {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    // Slim dart / winged arrowhead pointing downward
    val path = Path().apply {
        moveTo(cx, h * 0.88f)
        lineTo(w * 0.82f, h * 0.32f)
        lineTo(cx, h * 0.45f)
        lineTo(w * 0.18f, h * 0.32f)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(ScoutRust, Color(0xFF8C3916)),
            startY = h * 0.3f,
            endY = h * 0.9f
        ),
        style = Fill
    )

    drawPath(
        path = path,
        color = Color(0xFFFFD5B8).copy(alpha = 0.75f),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawBruteToken() {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h * 0.60f

    // Heavy Octagon / Fortified Iron Plate
    val path = Path().apply {
        val r = w * 0.34f
        val offset = r * 0.42f
        moveTo(cx - offset, cy - r)
        lineTo(cx + offset, cy - r)
        lineTo(cx + r, cy - offset)
        lineTo(cx + r, cy + offset)
        lineTo(cx + offset, cy + r)
        lineTo(cx - offset, cy + r)
        lineTo(cx - r, cy + offset)
        lineTo(cx - r, cy - offset)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(BruteBloodstone, Color(0xFF4C1010)),
            startY = cy - w * 0.34f,
            endY = cy + w * 0.34f
        ),
        style = Fill
    )

    drawPath(
        path = path,
        color = Color(0xFFE57373).copy(alpha = 0.8f),
        style = Stroke(width = 2.5f)
    )

    // Heavy iron cross / horn emblem
    drawLine(
        color = DefenderIvory.copy(alpha = 0.85f),
        start = Offset(cx - w * 0.18f, cy),
        end = Offset(cx + w * 0.18f, cy),
        strokeWidth = 3f
    )
    drawLine(
        color = DefenderIvory.copy(alpha = 0.85f),
        start = Offset(cx, cy - h * 0.18f),
        end = Offset(cx, cy + h * 0.18f),
        strokeWidth = 3f
    )
}

private fun DrawScope.drawHpPips(hp: Int, maxHp: Int) {
    val pipWidth = 6f
    val pipHeight = 3.5f
    val spacing = 3f
    val totalWidth = (maxHp * pipWidth) + ((maxHp - 1) * spacing)
    val startX = (size.width - totalWidth) / 2f
    val y = 5f

    for (i in 0 until maxHp) {
        val x = startX + (i * (pipWidth + spacing))
        val isFilled = i < hp
        drawRect(
            color = if (isFilled) Color(0xFF22C55E) else Color(0xFF334155),
            topLeft = Offset(x, y),
            size = Size(pipWidth, pipHeight)
        )
    }
}

// -------------------------------------------------------------
// CASTLE DRAWING
// -------------------------------------------------------------

private fun DrawScope.drawCastleBase() {
    val w = size.width
    val h = size.height

    // Stone foundation
    val baseRect = Path().apply {
        moveTo(w * 0.15f, h * 0.28f)
        lineTo(w * 0.85f, h * 0.28f)
        lineTo(w * 0.90f, h * 0.88f)
        lineTo(w * 0.10f, h * 0.88f)
        close()
    }
    drawPath(
        path = baseRect,
        brush = Brush.verticalGradient(
            colors = listOf(CastleStone, Color(0xFF222832)),
            startY = h * 0.28f,
            endY = h * 0.88f
        ),
        style = Fill
    )
    drawPath(
        path = baseRect,
        color = CastleGoldSecondary.copy(alpha = 0.7f),
        style = Stroke(width = 2f)
    )

    // Three crenelation battlements
    val bWidth = w * 0.16f
    val bHeight = h * 0.14f
    val bY = h * 0.16f

    val battlements = listOf(w * 0.14f, w * 0.42f, w * 0.70f)
    for (bx in battlements) {
        drawRect(
            color = CastleStone,
            topLeft = Offset(bx, bY),
            size = Size(bWidth, bHeight)
        )
        drawRect(
            color = CastleGoldSecondary.copy(alpha = 0.6f),
            topLeft = Offset(bx, bY),
            size = Size(bWidth, bHeight),
            style = Stroke(width = 1.5f)
        )
    }

    // Castle Gate arch
    val gatePath = Path().apply {
        moveTo(w * 0.40f, h * 0.88f)
        lineTo(w * 0.40f, h * 0.65f)
        quadraticTo(w * 0.50f, h * 0.55f, w * 0.60f, h * 0.65f)
        lineTo(w * 0.60f, h * 0.88f)
        close()
    }
    drawPath(path = gatePath, color = MidnightBackground, style = Fill)
    drawPath(path = gatePath, color = CastleGoldPrimary.copy(alpha = 0.7f), style = Stroke(width = 1.5f))
}
