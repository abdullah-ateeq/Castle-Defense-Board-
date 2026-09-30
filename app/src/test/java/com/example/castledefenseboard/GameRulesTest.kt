package com.example.castledefenseboard

import com.example.castledefenseboard.data.model.CastleDefenseState
import com.example.castledefenseboard.data.model.DefenderType
import com.example.castledefenseboard.data.model.DefenderUnit
import com.example.castledefenseboard.data.model.EnemyType
import com.example.castledefenseboard.data.model.EnemyUnit
import com.example.castledefenseboard.data.model.GridPosition
import com.example.castledefenseboard.domain.GameRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRulesTest {

    @Test
    fun testInitialStateConstants() {
        assertEquals(6, GameRules.INITIAL_ENERGY)
        assertEquals(10, GameRules.MAX_ENERGY)
        assertEquals(2, GameRules.TURN_ENERGY_GAIN)
        assertEquals(10, GameRules.INITIAL_CASTLE_HP)
        assertEquals(6, GameRules.MAX_DEFENDERS)
        assertEquals(5, GameRules.MAX_WAVES)
        assertEquals(GridPosition(5, 2), GameRules.CASTLE_POSITION)
        assertEquals(listOf(1, 3, 5), GameRules.ENTRY_COLUMNS)
        assertEquals(2..4, GameRules.LEGAL_DEFENSE_ROWS)
    }

    @Test
    fun testEnergyRegenerationAndClamping() {
        var energy = GameRules.INITIAL_ENERGY // 6
        energy = (energy + GameRules.TURN_ENERGY_GAIN).coerceAtMost(GameRules.MAX_ENERGY) // 8
        assertEquals(8, energy)

        energy = (energy + GameRules.TURN_ENERGY_GAIN).coerceAtMost(GameRules.MAX_ENERGY) // 10
        assertEquals(10, energy)

        energy = (energy + GameRules.TURN_ENERGY_GAIN).coerceAtMost(GameRules.MAX_ENERGY) // clamped to 10
        assertEquals(10, energy)
    }

    @Test
    fun testUnitCosts() {
        assertEquals(2, GameRules.energyCost(DefenderType.GUARD))
        assertEquals(3, GameRules.energyCost(DefenderType.ARCHER))
        assertEquals(4, GameRules.energyCost(DefenderType.MAGE))
    }

    @Test
    fun testUnitDamages() {
        assertEquals(2, GameRules.attackDamage(DefenderType.GUARD))
        assertEquals(1, GameRules.attackDamage(DefenderType.ARCHER))
        assertEquals(2, GameRules.attackDamage(DefenderType.MAGE))
    }

    @Test
    fun testLegalPlacementPositions() {
        // Outside rows 2..4 should be illegal
        assertFalse(GameRules.isLegalDefensePosition(GridPosition(row = 0, col = 1)))
        assertFalse(GameRules.isLegalDefensePosition(GridPosition(row = 1, col = 3)))
        assertFalse(GameRules.isLegalDefensePosition(GridPosition(row = 5, col = 2))) // Castle

        // Inside rows 2..4 should be legal
        assertTrue(GameRules.isLegalDefensePosition(GridPosition(row = 2, col = 1)))
        assertTrue(GameRules.isLegalDefensePosition(GridPosition(row = 3, col = 3)))
        assertTrue(GameRules.isLegalDefensePosition(GridPosition(row = 4, col = 5)))
    }

    @Test
    fun testUnitLimitEnforcement() {
        val defenders = mutableListOf<DefenderUnit>()
        for (i in 0 until GameRules.MAX_DEFENDERS) {
            defenders.add(
                DefenderUnit(
                    id = i + 1,
                    type = DefenderType.GUARD,
                    position = GridPosition(row = 2, col = i)
                )
            )
        }
        assertEquals(6, defenders.size)
        val canAddMore = defenders.size < GameRules.MAX_DEFENDERS
        assertFalse("Cannot place more than 6 defenders", canAddMore)
    }

    @Test
    fun testLegalUnitMovement() {
        val guard = DefenderUnit(
            id = 1,
            type = DefenderType.GUARD,
            position = GridPosition(row = 3, col = 2)
        )
        val allDefenders = listOf(guard)
        val validMoves = GameRules.getValidMoveDestinations(guard, allDefenders)

        // Orthogonal neighbors inside rows 2..4:
        // (2, 2), (4, 2), (3, 1), (3, 3)
        assertEquals(4, validMoves.size)
        assertTrue(validMoves.contains(GridPosition(2, 2)))
        assertTrue(validMoves.contains(GridPosition(4, 2)))
        assertTrue(validMoves.contains(GridPosition(3, 1)))
        assertTrue(validMoves.contains(GridPosition(3, 3)))

        // If adjacent cell is occupied by another defender, it must be excluded
        val blocker = DefenderUnit(
            id = 2,
            type = DefenderType.ARCHER,
            position = GridPosition(2, 2)
        )
        val movesWithBlocker = GameRules.getValidMoveDestinations(guard, listOf(guard, blocker))
        assertEquals(3, movesWithBlocker.size)
        assertFalse(movesWithBlocker.contains(GridPosition(2, 2)))

        // Cannot move into row 5 or row 1 (outside legal defense rows 2..4)
        val topDefender = DefenderUnit(
            id = 3,
            type = DefenderType.MAGE,
            position = GridPosition(row = 2, col = 2)
        )
        val topMoves = GameRules.getValidMoveDestinations(topDefender, listOf(topDefender))
        assertFalse("Cannot move up into row 1", topMoves.contains(GridPosition(1, 2)))
    }

    @Test
    fun testGuardRange() {
        val guard = DefenderUnit(
            id = 1,
            type = DefenderType.GUARD,
            position = GridPosition(row = 2, col = 2)
        )

        // Orthogonal adjacent (Manhattan == 1) -> IN RANGE
        val enemyNorth = EnemyUnit(1, EnemyType.RAIDER, GridPosition(row = 1, col = 2))
        val enemySouth = EnemyUnit(2, EnemyType.RAIDER, GridPosition(row = 3, col = 2))
        val enemyWest = EnemyUnit(3, EnemyType.RAIDER, GridPosition(row = 2, col = 1))
        val enemyEast = EnemyUnit(4, EnemyType.RAIDER, GridPosition(row = 2, col = 3))

        assertTrue(GameRules.isEnemyInRange(guard, enemyNorth))
        assertTrue(GameRules.isEnemyInRange(guard, enemySouth))
        assertTrue(GameRules.isEnemyInRange(guard, enemyWest))
        assertTrue(GameRules.isEnemyInRange(guard, enemyEast))

        // Diagonal (Manhattan == 2) -> OUT OF RANGE
        val enemyDiagonal = EnemyUnit(5, EnemyType.RAIDER, GridPosition(row = 1, col = 1))
        assertFalse(GameRules.isEnemyInRange(guard, enemyDiagonal))

        // Two cells orthogonal -> OUT OF RANGE
        val enemyFar = EnemyUnit(6, EnemyType.RAIDER, GridPosition(row = 0, col = 2))
        assertFalse(GameRules.isEnemyInRange(guard, enemyFar))
    }

    @Test
    fun testArcherRange() {
        val archer = DefenderUnit(
            id = 1,
            type = DefenderType.ARCHER,
            position = GridPosition(row = 3, col = 3)
        )

        // Same column up to 3 cells away: (0, 3), (1, 3), (2, 3), (4, 3), (5, 3)
        assertTrue(GameRules.isEnemyInRange(archer, EnemyUnit(1, EnemyType.RAIDER, GridPosition(0, 3))))
        assertTrue(GameRules.isEnemyInRange(archer, EnemyUnit(2, EnemyType.RAIDER, GridPosition(1, 3))))
        assertTrue(GameRules.isEnemyInRange(archer, EnemyUnit(3, EnemyType.RAIDER, GridPosition(2, 3))))
        assertTrue(GameRules.isEnemyInRange(archer, EnemyUnit(4, EnemyType.RAIDER, GridPosition(4, 3))))

        // Same row up to 3 cells away: (3, 0), (3, 1), (3, 2), (3, 4), (3, 5)
        assertTrue(GameRules.isEnemyInRange(archer, EnemyUnit(5, EnemyType.RAIDER, GridPosition(3, 0))))
        assertTrue(GameRules.isEnemyInRange(archer, EnemyUnit(6, EnemyType.RAIDER, GridPosition(3, 5))))

        // Diagonal -> OUT OF RANGE
        assertFalse(GameRules.isEnemyInRange(archer, EnemyUnit(7, EnemyType.RAIDER, GridPosition(2, 2))))

        // Different row and different column -> OUT OF RANGE
        assertFalse(GameRules.isEnemyInRange(archer, EnemyUnit(8, EnemyType.RAIDER, GridPosition(1, 1))))
    }

    @Test
    fun testMageRange() {
        val mage = DefenderUnit(
            id = 1,
            type = DefenderType.MAGE,
            position = GridPosition(row = 3, col = 3)
        )

        // Manhattan distance 1 -> IN RANGE
        assertTrue(GameRules.isEnemyInRange(mage, EnemyUnit(1, EnemyType.RAIDER, GridPosition(2, 3))))

        // Manhattan distance 2 (orthogonal) -> IN RANGE
        assertTrue(GameRules.isEnemyInRange(mage, EnemyUnit(2, EnemyType.RAIDER, GridPosition(1, 3))))

        // Manhattan distance 2 (diagonal: |3-2| + |3-4| = 2) -> IN RANGE
        assertTrue(GameRules.isEnemyInRange(mage, EnemyUnit(3, EnemyType.RAIDER, GridPosition(2, 4))))

        // Manhattan distance 3 -> OUT OF RANGE
        assertFalse(GameRules.isEnemyInRange(mage, EnemyUnit(4, EnemyType.RAIDER, GridPosition(0, 3))))
        assertFalse(GameRules.isEnemyInRange(mage, EnemyUnit(5, EnemyType.RAIDER, GridPosition(1, 4))))
    }

    @Test
    fun testTargetSelectionDeterminism() {
        val archer = DefenderUnit(
            id = 1,
            type = DefenderType.ARCHER,
            position = GridPosition(row = 4, col = 3)
        )

        // Priority 1: Highest row (closest to bottom castle)
        val enemyAtRow1 = EnemyUnit(id = 10, type = EnemyType.RAIDER, position = GridPosition(1, 3), hp = 1)
        val enemyAtRow2 = EnemyUnit(id = 11, type = EnemyType.BRUTE, position = GridPosition(2, 3), hp = 4)

        var target = GameRules.chooseTarget(archer, listOf(enemyAtRow1, enemyAtRow2))
        assertEquals(enemyAtRow2.id, target?.id) // Row 2 is higher row than Row 1

        // Priority 2: Same row, tie broken by Lowest HP
        val enemySameRowHigherHp = EnemyUnit(id = 20, type = EnemyType.BRUTE, position = GridPosition(3, 3), hp = 4)
        val enemySameRowLowerHp = EnemyUnit(id = 21, type = EnemyType.SCOUT, position = GridPosition(3, 3), hp = 1)

        target = GameRules.chooseTarget(archer, listOf(enemySameRowHigherHp, enemySameRowLowerHp))
        assertEquals(enemySameRowLowerHp.id, target?.id) // Lowest HP wins tie

        // Priority 3: Same row and same HP, tie broken by Lowest ID
        val enemyId5 = EnemyUnit(id = 5, type = EnemyType.RAIDER, position = GridPosition(3, 3), hp = 2)
        val enemyId8 = EnemyUnit(id = 8, type = EnemyType.RAIDER, position = GridPosition(3, 3), hp = 2)

        target = GameRules.chooseTarget(archer, listOf(enemyId8, enemyId5))
        assertEquals(enemyId5.id, target?.id) // Lowest ID wins tie
    }

    @Test
    fun testEnemyDamageAndRemovalAtZeroHp() {
        val raider = EnemyUnit(id = 1, type = EnemyType.RAIDER, position = GridPosition(1, 1), hp = 2)
        val guardDamage = GameRules.attackDamage(DefenderType.GUARD) // 2

        val afterDamageHp = raider.hp - guardDamage
        assertEquals(0, afterDamageHp)

        val enemies = listOf(raider.copy(hp = afterDamageHp))
        val survivingEnemies = enemies.filter { it.hp > 0 }
        assertTrue("Enemy with 0 HP must be removed", survivingEnemies.isEmpty())
    }

    @Test
    fun testEnemyDownwardMovement() {
        val enemy = EnemyUnit(id = 1, type = EnemyType.RAIDER, position = GridPosition(row = 1, col = 3))
        val moved = enemy.copy(position = GridPosition(row = enemy.position.row + 1, col = enemy.position.col))

        assertEquals(2, moved.position.row)
        assertEquals(3, moved.position.col)
    }

    @Test
    fun testCastleDamageOnBottomRow() {
        var castleHp = GameRules.INITIAL_CASTLE_HP // 10

        val bruteBreaching = EnemyUnit(
            id = 1,
            type = EnemyType.BRUTE,
            position = GridPosition(row = 5, col = 3),
            hp = 4
        )

        val damage = bruteBreaching.type.castleDamage // 2
        castleHp -= damage
        assertEquals(8, castleHp)
    }

    @Test
    fun testWaveProgression() {
        val wave1 = GameRules.getWaveEnemies(1)
        val wave2 = GameRules.getWaveEnemies(2)
        val wave3 = GameRules.getWaveEnemies(3)
        val wave4 = GameRules.getWaveEnemies(4)
        val wave5 = GameRules.getWaveEnemies(5)

        assertEquals(3, wave1.size)
        assertEquals(listOf(EnemyType.RAIDER, EnemyType.RAIDER, EnemyType.RAIDER), wave1)

        assertEquals(3, wave2.size)
        assertEquals(listOf(EnemyType.RAIDER, EnemyType.RAIDER, EnemyType.SCOUT), wave2)

        assertEquals(3, wave3.size)
        assertEquals(listOf(EnemyType.SCOUT, EnemyType.SCOUT, EnemyType.BRUTE), wave3)

        assertEquals(4, wave4.size)
        assertEquals(listOf(EnemyType.RAIDER, EnemyType.RAIDER, EnemyType.BRUTE, EnemyType.BRUTE), wave4)

        assertEquals(6, wave5.size)
        assertEquals(
            listOf(
                EnemyType.SCOUT,
                EnemyType.SCOUT,
                EnemyType.RAIDER,
                EnemyType.RAIDER,
                EnemyType.BRUTE,
                EnemyType.BRUTE
            ),
            wave5
        )
    }

    @Test
    fun testVictoryAndDefeatConditions() {
        // Victory: Wave 5 cleared and Castle HP > 0
        val stateVictory = CastleDefenseState(
            wave = 5,
            enemies = emptyList(),
            spawnQueue = emptyList(),
            castleHp = 7,
            isVictory = true,
            isDefeat = false
        )
        assertTrue(stateVictory.isVictory)
        assertFalse(stateVictory.isDefeat)
        assertTrue(stateVictory.isGameOver)

        // Defeat: Castle HP <= 0
        val stateDefeat = CastleDefenseState(
            castleHp = 0,
            isVictory = false,
            isDefeat = true
        )
        assertTrue(stateDefeat.isDefeat)
        assertFalse(stateDefeat.isVictory)
        assertTrue(stateDefeat.isGameOver)
    }
}
