package com.example.castledefenseboard.data.model

data class CastleDefenseState(
    val defenders: List<DefenderUnit> = emptyList(),
    val enemies: List<EnemyUnit> = emptyList(),
    val spawnQueue: List<EnemyType> = emptyList(),
    val wave: Int = 1,
    val maxWaves: Int = 5,
    val castleHp: Int = 10,
    val maxCastleHp: Int = 10,
    val energy: Int = 6,
    val maxEnergy: Int = 10,
    val enemiesDefeated: Int = 0,
    val unitsDeployed: Int = 0,
    val energySpent: Int = 0,
    val turnCount: Int = 1,
    val isPlayerActionAvailable: Boolean = true,
    val isAnimating: Boolean = false,
    val isVictory: Boolean = false,
    val isDefeat: Boolean = false,
    val selectedDefenderType: DefenderType? = null,
    val selectedDefenderIdToMove: Int? = null,
    val activeAttackEvents: List<AttackEvent> = emptyList(),
    val activeCastleDamage: CastleDamageEvent? = null,
    val statusMessage: String = "Your Turn: Deploy a unit, move, or skip",
    val startTimeMillis: Long = System.currentTimeMillis(),
    val endTimeMillis: Long = 0L
) {
    val isGameOver: Boolean get() = isVictory || isDefeat
    val durationSeconds: Long
        get() {
            val end = if (endTimeMillis > 0) endTimeMillis else System.currentTimeMillis()
            return ((end - startTimeMillis) / 1000).coerceAtLeast(0)
        }
}
