package com.example.castledefenseboard.data.model

data class GridPosition(
    val row: Int,
    val col: Int
) {
    fun manhattanDistance(other: GridPosition): Int {
        return kotlin.math.abs(row - other.row) + kotlin.math.abs(col - other.col)
    }

    fun isOrthogonalAdjacent(other: GridPosition): Boolean {
        return manhattanDistance(other) == 1
    }
}
