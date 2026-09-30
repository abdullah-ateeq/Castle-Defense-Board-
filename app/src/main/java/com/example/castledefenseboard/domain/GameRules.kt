package com.example.castledefenseboard.domain

import com.example.castledefenseboard.data.model.DefenderType
import com.example.castledefenseboard.data.model.DefenderUnit
import com.example.castledefenseboard.data.model.EnemyType
import com.example.castledefenseboard.data.model.EnemyUnit
import com.example.castledefenseboard.data.model.GridPosition
import kotlin.math.abs

object GameRules {
    const val GRID_ROWS = 6
    const val GRID_COLS = 6

    val CASTLE_POSITION = GridPosition(row = 5, col = 2)
    val ENTRY_COLUMNS = listOf(1, 3, 5)
    val LEGAL_DEFENSE_ROWS = 2..4

    const val INITIAL_ENERGY = 6
    const val TURN_ENERGY_GAIN = 2
    const val MAX_ENERGY = 10
    const val MOVE_COST = 1
    const val SKIP_COST = 0
    const val MAX_DEFENDERS = 6
    const val INITIAL_CASTLE_HP = 10
    const val MAX_WAVES = 5

    fun energyCost(type: DefenderType): Int = type.cost

    fun attackDamage(type: DefenderType): Int = type.damage

    fun isLegalDefensePosition(position: GridPosition): Boolean {
        if (position.row !in LEGAL_DEFENSE_ROWS) return false
        if (position.col !in 0 until GRID_COLS) return false
        if (position == CASTLE_POSITION) return false
        return true
    }

    fun isEnemyInRange(defender: DefenderUnit, enemy: EnemyUnit): Boolean {
        val defPos = defender.position
        val enPos = enemy.position

        val rowDist = abs(defPos.row - enPos.row)
        val colDist = abs(defPos.col - enPos.col)
        val manhattan = rowDist + colDist

        if (manhattan == 0) return false

        return when (defender.type) {
            DefenderType.GUARD -> {
                manhattan == 1
            }
            DefenderType.ARCHER -> {
                (defPos.row == enPos.row && colDist in 1..3) ||
                        (defPos.col == enPos.col && rowDist in 1..3)
            }
            DefenderType.MAGE -> {
                manhattan in 1..2
            }
        }
    }

    fun chooseTarget(
        defender: DefenderUnit,
        enemies: List<EnemyUnit>
    ): EnemyUnit? {
        val inRange = enemies.filter { enemy ->
            enemy.hp > 0 && isEnemyInRange(defender, enemy)
        }

        if (inRange.isEmpty()) return null

        return inRange.sortedWith(
            compareByDescending<EnemyUnit> { it.position.row } // 1. Highest row (closest to bottom castle)
                .thenBy { it.hp }                              // 2. Lowest HP
                .thenBy { it.id }                              // 3. Lowest ID
        ).first()
    }

    fun getValidPlacementPositions(defenders: List<DefenderUnit>): List<GridPosition> {
        val occupied = defenders.map { it.position }.toSet()
        val validList = mutableListOf<GridPosition>()

        for (row in LEGAL_DEFENSE_ROWS) {
            for (col in 0 until GRID_COLS) {
                val pos = GridPosition(row, col)
                if (pos !in occupied && pos != CASTLE_POSITION) {
                    validList.add(pos)
                }
            }
        }
        return validList
    }

    fun getValidMoveDestinations(
        defender: DefenderUnit,
        allDefenders: List<DefenderUnit>
    ): List<GridPosition> {
        val occupied = allDefenders.map { it.position }.toSet()
        val orthogonalOffsets = listOf(
            GridPosition(-1, 0),
            GridPosition(1, 0),
            GridPosition(0, -1),
            GridPosition(0, 1)
        )

        return orthogonalOffsets.map { offset ->
            GridPosition(defender.position.row + offset.row, defender.position.col + offset.col)
        }.filter { pos ->
            isLegalDefensePosition(pos) && pos !in occupied
        }
    }

    fun getAttackRangeCells(defender: DefenderUnit): List<GridPosition> {
        val cells = mutableListOf<GridPosition>()
        for (r in 0 until GRID_ROWS) {
            for (c in 0 until GRID_COLS) {
                val pos = GridPosition(r, c)
                if (pos == defender.position) continue

                val dummyEnemy = EnemyUnit(
                    id = -1,
                    type = EnemyType.RAIDER,
                    position = pos,
                    hp = 1
                )
                if (isEnemyInRange(defender, dummyEnemy)) {
                    cells.add(pos)
                }
            }
        }
        return cells
    }

    fun getWaveEnemies(wave: Int): List<EnemyType> {
        return when (wave) {
            1 -> listOf(
                EnemyType.RAIDER,
                EnemyType.RAIDER,
                EnemyType.RAIDER
            )
            2 -> listOf(
                EnemyType.RAIDER,
                EnemyType.RAIDER,
                EnemyType.SCOUT
            )
            3 -> listOf(
                EnemyType.SCOUT,
                EnemyType.SCOUT,
                EnemyType.BRUTE
            )
            4 -> listOf(
                EnemyType.RAIDER,
                EnemyType.RAIDER,
                EnemyType.BRUTE,
                EnemyType.BRUTE
            )
            5 -> listOf(
                EnemyType.SCOUT,
                EnemyType.SCOUT,
                EnemyType.RAIDER,
                EnemyType.RAIDER,
                EnemyType.BRUTE,
                EnemyType.BRUTE
            )
            else -> emptyList()
        }
    }
}
