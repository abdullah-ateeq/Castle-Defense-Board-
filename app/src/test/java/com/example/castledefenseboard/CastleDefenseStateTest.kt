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
import org.junit.Assert.assertTrue
import org.junit.Test

class CastleDefenseStateTest {

    @Test
    fun testDefaultStateValues() {
        val state = CastleDefenseState()
        assertEquals(1, state.wave)
        assertEquals(5, state.maxWaves)
        assertEquals(10, state.castleHp)
        assertEquals(10, state.maxCastleHp)
        assertEquals(6, state.energy)
        assertEquals(10, state.maxEnergy)
        assertEquals(0, state.enemiesDefeated)
        assertEquals(0, state.unitsDeployed)
        assertEquals(0, state.energySpent)
        assertEquals(1, state.turnCount)
        assertTrue(state.isPlayerActionAvailable)
        assertFalse(state.isAnimating)
        assertFalse(state.isVictory)
        assertFalse(state.isDefeat)
        assertFalse(state.isGameOver)
    }

    @Test
    fun testDurationCalculation() {
        val start = System.currentTimeMillis() - 65_000 // 65 seconds ago
        val state = CastleDefenseState(
            startTimeMillis = start,
            endTimeMillis = start + 65_000
        )
        assertEquals(65L, state.durationSeconds)
    }

    @Test
    fun testDefenderPlacementSimulation() {
        var state = CastleDefenseState()
        val guardCost = GameRules.energyCost(DefenderType.GUARD)

        // Player places a Guard
        val placedGuard = DefenderUnit(id = 1, type = DefenderType.GUARD, position = GridPosition(3, 2))
        state = state.copy(
            defenders = state.defenders + placedGuard,
            energy = state.energy - guardCost,
            unitsDeployed = state.unitsDeployed + 1,
            energySpent = state.energySpent + guardCost,
            isPlayerActionAvailable = false
        )

        assertEquals(1, state.defenders.size)
        assertEquals(4, state.energy) // 6 - 2 = 4
        assertEquals(1, state.unitsDeployed)
        assertEquals(2, state.energySpent)
        assertFalse(state.isPlayerActionAvailable)
    }

    @Test
    fun testDefenderMoveSimulation() {
        val initialGuard = DefenderUnit(id = 1, type = DefenderType.GUARD, position = GridPosition(3, 2))
        var state = CastleDefenseState(
            defenders = listOf(initialGuard),
            energy = 4
        )

        // Player moves the Guard to (3, 3)
        val targetPos = GridPosition(3, 3)
        val validDestinations = GameRules.getValidMoveDestinations(initialGuard, state.defenders)
        assertTrue(validDestinations.contains(targetPos))

        val movedDefenders = state.defenders.map {
            if (it.id == initialGuard.id) it.copy(position = targetPos) else it
        }

        state = state.copy(
            defenders = movedDefenders,
            energy = state.energy - GameRules.MOVE_COST,
            energySpent = state.energySpent + GameRules.MOVE_COST,
            isPlayerActionAvailable = false
        )

        assertEquals(targetPos, state.defenders.first().position)
        assertEquals(3, state.energy)
        assertEquals(1, state.energySpent)
        assertFalse(state.isPlayerActionAvailable)
    }
}
