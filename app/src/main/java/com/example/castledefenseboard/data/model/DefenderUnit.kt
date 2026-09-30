package com.example.castledefenseboard.data.model

enum class DefenderType(
    val title: String,
    val cost: Int,
    val damage: Int,
    val rangeDescription: String
) {
    GUARD(
        title = "Guard",
        cost = 2,
        damage = 2,
        rangeDescription = "1 Orthogonal cell"
    ),
    ARCHER(
        title = "Archer",
        cost = 3,
        damage = 1,
        rangeDescription = "Up to 3 cells in row or col"
    ),
    MAGE(
        title = "Mage",
        cost = 4,
        damage = 2,
        rangeDescription = "2 cells (Manhattan distance)"
    )
}

data class DefenderUnit(
    val id: Int,
    val type: DefenderType,
    val position: GridPosition
)
