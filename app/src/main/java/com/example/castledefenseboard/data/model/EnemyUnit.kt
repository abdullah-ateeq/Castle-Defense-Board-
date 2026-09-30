package com.example.castledefenseboard.data.model

enum class EnemyType(
    val title: String,
    val initialHp: Int,
    val castleDamage: Int,
    val description: String
) {
    RAIDER(
        title = "Raider",
        initialHp = 2,
        castleDamage = 1,
        description = "Standard infantry. 2 HP, deals 1 damage to castle."
    ),
    SCOUT(
        title = "Scout",
        initialHp = 1,
        castleDamage = 1,
        description = "Fragile vanguard. 1 HP, deals 1 damage to castle."
    ),
    BRUTE(
        title = "Brute",
        initialHp = 4,
        castleDamage = 2,
        description = "Heavy armored shock trooper. 4 HP, deals 2 damage to castle."
    )
}

data class EnemyUnit(
    val id: Int,
    val type: EnemyType,
    val position: GridPosition,
    val hp: Int = type.initialHp,
    val maxHp: Int = type.initialHp
)
