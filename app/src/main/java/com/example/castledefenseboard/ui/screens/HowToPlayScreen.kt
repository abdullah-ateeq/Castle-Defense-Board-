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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.castledefenseboard.data.model.DefenderType
import com.example.castledefenseboard.data.model.DefenderUnit
import com.example.castledefenseboard.data.model.EnemyType
import com.example.castledefenseboard.data.model.EnemyUnit
import com.example.castledefenseboard.data.model.GridPosition
import com.example.castledefenseboard.ui.components.DefenderToken
import com.example.castledefenseboard.ui.components.EnemyToken
import com.example.castledefenseboard.ui.theme.CastleGoldPrimary
import com.example.castledefenseboard.ui.theme.CastleGoldSecondary
import com.example.castledefenseboard.ui.theme.EnergyAmber
import com.example.castledefenseboard.ui.theme.MidnightBackground
import com.example.castledefenseboard.ui.theme.MidnightCard
import com.example.castledefenseboard.ui.theme.MidnightElevated
import com.example.castledefenseboard.ui.theme.StoneBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HOW TO PLAY",
                        color = CastleGoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CastleGoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightBackground
                )
            )
        },
        containerColor = MidnightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Objective Section
            SectionCard(title = "OBJECTIVE") {
                Text(
                    text = "Survive all 5 enemy waves and protect your Castle base at the bottom of the 6×6 tactical board. The castle starts with 10 HP. If the castle reaches 0 HP, the defense is lost.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 19.sp
                )
            }

            // Energy & Actions Section
            SectionCard(title = "ENERGY & TURN RULES") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• You begin the battle with 6⚡ energy.\n" +
                                "• Gain +2⚡ at the start of each turn (capped at 10⚡).\n" +
                                "• Each turn, you may perform exactly ONE action:\n" +
                                "   1. Deploy Unit: Spend energy to place a defender in rows 3, 4, or 5 (max 6 defenders).\n" +
                                "   2. Move Unit: Spend 1⚡ to move an existing defender 1 cell orthogonally.\n" +
                                "   3. Skip Turn: Spend 0⚡ to conserve energy.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                }
            }

            // Turn Resolution Order
            SectionCard(title = "TURN RESOLUTION") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "1. Player Action (Deploy, Move, or Skip)\n" +
                                "2. Automatic Defender Attacks: Every defender fires at the closest valid enemy in its range.\n" +
                                "3. Enemy Advance: Surviving enemies march 1 row straight downward.\n" +
                                "4. Castle Breach: Any enemy reaching the bottom row inflicts damage on the castle and is removed.\n" +
                                "5. Wave Progress: Next turn begins with +2⚡ energy and reinforcement spawns.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                }
            }

            // Defender Units
            SectionCard(title = "DEFENDER UNITS") {
                Text(
                    text = "Defenders act as permanent tactical pieces. They do not have HP and cannot be destroyed by enemies.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                DefenderRow(
                    defenderType = DefenderType.GUARD,
                    cost = 2,
                    damage = 2,
                    range = "1 Orthogonal cell",
                    description = "Compact melee guard. High damage output on adjacent cells."
                )
                Spacer(modifier = Modifier.height(8.dp))

                DefenderRow(
                    defenderType = DefenderType.ARCHER,
                    cost = 3,
                    damage = 1,
                    range = "Up to 3 cells (same row/col)",
                    description = "Long range sniper. Commands full lanes and crossfire."
                )
                Spacer(modifier = Modifier.height(8.dp))

                DefenderRow(
                    defenderType = DefenderType.MAGE,
                    cost = 4,
                    damage = 2,
                    range = "Manhattan distance ≤ 2",
                    description = "High arcane firepower covering diagonal and perimeter threats."
                )
            }

            // Enemy Units
            SectionCard(title = "ENEMY FORCES") {
                EnemyRow(
                    enemyType = EnemyType.RAIDER,
                    hp = 2,
                    castleDamage = 1,
                    description = "Standard infantry force marching in steady formation."
                )
                Spacer(modifier = Modifier.height(8.dp))

                EnemyRow(
                    enemyType = EnemyType.SCOUT,
                    hp = 1,
                    castleDamage = 1,
                    description = "Fast fragile vanguard easily eliminated by archer volleys."
                )
                Spacer(modifier = Modifier.height(8.dp))

                EnemyRow(
                    enemyType = EnemyType.BRUTE,
                    hp = 4,
                    castleDamage = 2,
                    description = "Armored behemoth with 4 HP that deals severe 2 HP damage to the castle."
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StoneBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CastleGoldSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun DefenderRow(
    defenderType: DefenderType,
    cost: Int,
    damage: Int,
    range: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MidnightElevated)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            DefenderToken(
                defender = DefenderUnit(
                    id = 1,
                    type = defenderType,
                    position = GridPosition(0, 0)
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = defenderType.title.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CastleGoldPrimary
                )
                Text(
                    text = "Cost: $cost⚡ | Dmg: $damage",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EnergyAmber
                )
            }
            Text(
                text = "Range: $range",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EnemyRow(
    enemyType: EnemyType,
    hp: Int,
    castleDamage: Int,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MidnightElevated)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            EnemyToken(
                enemy = EnemyUnit(
                    id = 1,
                    type = enemyType,
                    position = GridPosition(0, 0),
                    hp = hp,
                    maxHp = hp
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = enemyType.title.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE57373)
                )
                Text(
                    text = "HP: $hp | Castle Dmg: $castleDamage",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF87171)
                )
            }
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
