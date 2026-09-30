package com.example.castledefenseboard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.castledefenseboard.audio.HapticsHelper
import com.example.castledefenseboard.audio.SoundSynthesizer
import com.example.castledefenseboard.data.model.AttackEvent
import com.example.castledefenseboard.data.model.CastleDamageEvent
import com.example.castledefenseboard.data.model.CastleDefenseState
import com.example.castledefenseboard.data.model.DefenderType
import com.example.castledefenseboard.data.model.DefenderUnit
import com.example.castledefenseboard.data.model.EnemyType
import com.example.castledefenseboard.data.model.EnemyUnit
import com.example.castledefenseboard.data.model.GridPosition
import com.example.castledefenseboard.data.preferences.GamePreferencesRepository
import com.example.castledefenseboard.data.preferences.GameSettings
import com.example.castledefenseboard.domain.GameRules
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GamePreferencesRepository(application)
    private val soundSynthesizer = SoundSynthesizer()
    private val hapticsHelper = HapticsHelper(application)

    private val _gameState = MutableStateFlow(CastleDefenseState())
    val gameState: StateFlow<CastleDefenseState> = _gameState.asStateFlow()

    private var nextDefenderId = 1
    private var nextEnemyId = 1

    init {
        startNewGame()
    }

    fun startNewGame() {
        nextDefenderId = 1
        nextEnemyId = 1

        val wave1Queue = GameRules.getWaveEnemies(1).toMutableList()
        val initialEnemies = mutableListOf<EnemyUnit>()

        // Spawn first batch of enemies across available entry columns [1, 3, 5] on row 0
        for (col in GameRules.ENTRY_COLUMNS) {
            if (wave1Queue.isNotEmpty()) {
                val enemyType = wave1Queue.removeAt(0)
                initialEnemies.add(
                    EnemyUnit(
                        id = nextEnemyId++,
                        type = enemyType,
                        position = GridPosition(0, col),
                        hp = enemyType.initialHp,
                        maxHp = enemyType.initialHp
                    )
                )
            }
        }

        _gameState.value = CastleDefenseState(
            defenders = emptyList(),
            enemies = initialEnemies,
            spawnQueue = wave1Queue,
            wave = 1,
            maxWaves = GameRules.MAX_WAVES,
            castleHp = GameRules.INITIAL_CASTLE_HP,
            maxCastleHp = GameRules.INITIAL_CASTLE_HP,
            energy = GameRules.INITIAL_ENERGY,
            maxEnergy = GameRules.MAX_ENERGY,
            enemiesDefeated = 0,
            unitsDeployed = 0,
            energySpent = 0,
            turnCount = 1,
            isPlayerActionAvailable = true,
            isAnimating = false,
            isVictory = false,
            isDefeat = false,
            selectedDefenderType = null,
            selectedDefenderIdToMove = null,
            activeAttackEvents = emptyList(),
            activeCastleDamage = null,
            statusMessage = "Wave 1 - Place a defender, move, or skip turn",
            startTimeMillis = System.currentTimeMillis(),
            endTimeMillis = 0L
        )
    }

    fun selectDefenderType(type: DefenderType?) {
        val current = _gameState.value
        if (current.isGameOver || current.isAnimating || !current.isPlayerActionAvailable) return

        if (current.selectedDefenderType == type) {
            _gameState.update { it.copy(selectedDefenderType = null) }
        } else {
            _gameState.update {
                it.copy(
                    selectedDefenderType = type,
                    selectedDefenderIdToMove = null,
                    statusMessage = "Select an empty cell in rows 3-5 to place ${type?.title}"
                )
            }
        }
    }

    fun selectDefenderToMove(defenderId: Int?) {
        val current = _gameState.value
        if (current.isGameOver || current.isAnimating || !current.isPlayerActionAvailable) return

        if (current.selectedDefenderIdToMove == defenderId) {
            _gameState.update {
                it.copy(
                    selectedDefenderIdToMove = null,
                    statusMessage = "Selection cleared"
                )
            }
        } else {
            val defender = current.defenders.find { it.id == defenderId }
            _gameState.update {
                it.copy(
                    selectedDefenderIdToMove = defenderId,
                    selectedDefenderType = null,
                    statusMessage = if (defender != null) {
                        "${defender.type.title} selected: Tap adjacent cell to move (1 energy)"
                    } else {
                        "Defender selected"
                    }
                )
            }
        }
    }

    fun onCellClicked(position: GridPosition) {
        val state = _gameState.value
        if (state.isGameOver || state.isAnimating || !state.isPlayerActionAvailable) return

        // 1. Placement Mode
        val placingType = state.selectedDefenderType
        if (placingType != null) {
            handlePlaceUnit(placingType, position)
            return
        }

        // 2. Unit Selection or Move Mode
        val clickedDefender = state.defenders.find { it.position == position }
        if (clickedDefender != null) {
            selectDefenderToMove(clickedDefender.id)
            return
        }

        val movingDefenderId = state.selectedDefenderIdToMove
        if (movingDefenderId != null) {
            handleMoveUnit(movingDefenderId, position)
            return
        }
    }

    private fun handlePlaceUnit(type: DefenderType, position: GridPosition) {
        val state = _gameState.value
        val cost = GameRules.energyCost(type)

        if (state.energy < cost) {
            _gameState.update { it.copy(statusMessage = "Not enough energy! Needs $cost⚡") }
            return
        }
        if (state.defenders.size >= GameRules.MAX_DEFENDERS) {
            _gameState.update { it.copy(statusMessage = "Defender limit reached (${GameRules.MAX_DEFENDERS} max)!") }
            return
        }
        if (!GameRules.isLegalDefensePosition(position)) {
            _gameState.update { it.copy(statusMessage = "Must place in defense rows 3, 4, or 5!") }
            return
        }
        if (state.defenders.any { it.position == position }) {
            _gameState.update { it.copy(statusMessage = "Cell already occupied by another defender!") }
            return
        }

        // Action is legal, execute placement
        val newDefender = DefenderUnit(
            id = nextDefenderId++,
            type = type,
            position = position
        )

        viewModelScope.launch {
            val settings = repository.settingsFlow.first()
            if (settings.soundEnabled) soundSynthesizer.playUnitPlaced()
            hapticsHelper.vibratePlacement(settings.hapticsEnabled)
        }

        _gameState.update { current ->
            current.copy(
                defenders = current.defenders + newDefender,
                energy = current.energy - cost,
                unitsDeployed = current.unitsDeployed + 1,
                energySpent = current.energySpent + cost,
                selectedDefenderType = null,
                selectedDefenderIdToMove = null,
                isPlayerActionAvailable = false,
                statusMessage = "Deployed ${type.title} at (${position.row + 1}, ${position.col + 1})"
            )
        }

        resolveTurnPhases()
    }

    private fun handleMoveUnit(defenderId: Int, destination: GridPosition) {
        val state = _gameState.value
        val defender = state.defenders.find { it.id == defenderId } ?: return

        if (state.energy < GameRules.MOVE_COST) {
            _gameState.update { it.copy(statusMessage = "Not enough energy to move (Needs 1⚡)!") }
            return
        }

        val legalDestinations = GameRules.getValidMoveDestinations(defender, state.defenders)
        if (destination !in legalDestinations) {
            // Deselect or notify
            selectDefenderToMove(null)
            return
        }

        viewModelScope.launch {
            val settings = repository.settingsFlow.first()
            if (settings.soundEnabled) soundSynthesizer.playUnitMoved()
            hapticsHelper.vibratePlacement(settings.hapticsEnabled)
        }

        val updatedDefenders = state.defenders.map {
            if (it.id == defenderId) it.copy(position = destination) else it
        }

        _gameState.update { current ->
            current.copy(
                defenders = updatedDefenders,
                energy = current.energy - GameRules.MOVE_COST,
                energySpent = current.energySpent + GameRules.MOVE_COST,
                selectedDefenderIdToMove = null,
                selectedDefenderType = null,
                isPlayerActionAvailable = false,
                statusMessage = "Moved ${defender.type.title} to (${destination.row + 1}, ${destination.col + 1})"
            )
        }

        resolveTurnPhases()
    }

    fun skipTurn() {
        val state = _gameState.value
        if (state.isGameOver || state.isAnimating || !state.isPlayerActionAvailable) return

        _gameState.update {
            it.copy(
                selectedDefenderType = null,
                selectedDefenderIdToMove = null,
                isPlayerActionAvailable = false,
                statusMessage = "Turn skipped. Defenders engaging..."
            )
        }

        resolveTurnPhases()
    }

    fun cancelSelection() {
        _gameState.update {
            it.copy(
                selectedDefenderType = null,
                selectedDefenderIdToMove = null,
                statusMessage = "Selection cancelled"
            )
        }
    }

    private fun resolveTurnPhases() {
        viewModelScope.launch {
            val settings = repository.settingsFlow.first()
            val delayScale = if (settings.fastAnimations) 0.5f else 1.0f

            _gameState.update { it.copy(isAnimating = true) }

            // -------------------------------------------------------------
            // PHASE 1: DEFENDER ATTACK PHASE
            // -------------------------------------------------------------
            val currentState = _gameState.value
            val sortedDefenders = currentState.defenders.sortedBy { it.id }
            val survivingEnemies = currentState.enemies.filter { it.hp > 0 }.toMutableList()
            var totalDefeatedInPhase = 0

            for (defender in sortedDefenders) {
                val target = GameRules.chooseTarget(defender, survivingEnemies)
                if (target != null) {
                    val damage = GameRules.attackDamage(defender.type)
                    val newHp = target.hp - damage
                    val enemyKilled = newHp <= 0

                    if (settings.soundEnabled) {
                        when (defender.type) {
                            DefenderType.GUARD -> soundSynthesizer.playGuardAttack()
                            DefenderType.ARCHER -> soundSynthesizer.playArcherAttack()
                            DefenderType.MAGE -> soundSynthesizer.playMageAttack()
                        }
                    }

                    if (enemyKilled) {
                        totalDefeatedInPhase++
                        survivingEnemies.removeIf { it.id == target.id }
                        if (settings.soundEnabled) soundSynthesizer.playEnemyDefeated()
                        hapticsHelper.vibrateEnemyDefeated(settings.hapticsEnabled)
                    } else {
                        val targetIndex = survivingEnemies.indexOfFirst { it.id == target.id }
                        if (targetIndex != -1) {
                            survivingEnemies[targetIndex] = target.copy(hp = newHp)
                        }
                    }

                    val attackEvent = AttackEvent(
                        defenderId = defender.id,
                        defenderPos = defender.position,
                        targetEnemyId = target.id,
                        targetPos = target.position,
                        damage = damage,
                        type = defender.type,
                        enemyKilled = enemyKilled
                    )

                    _gameState.update { state ->
                        state.copy(
                            enemies = survivingEnemies.toList(),
                            enemiesDefeated = state.enemiesDefeated + (if (enemyKilled) 1 else 0),
                            activeAttackEvents = listOf(attackEvent),
                            statusMessage = "${defender.type.title} attacks ${target.type.title} for $damage dmg!"
                        )
                    }

                    delay((180 * delayScale).toLong())
                }
            }

            // Clear active attack beams
            _gameState.update { it.copy(activeAttackEvents = emptyList()) }
            delay((80 * delayScale).toLong())

            // -------------------------------------------------------------
            // PHASE 2: ENEMY MOVEMENT PHASE
            // -------------------------------------------------------------
            _gameState.update { it.copy(statusMessage = "Enemies advancing...") }
            delay((120 * delayScale).toLong())

            // Move each surviving enemy downward: row + 1, same col
            val movedEnemies = survivingEnemies.map { enemy ->
                enemy.copy(position = GridPosition(enemy.position.row + 1, enemy.position.col))
            }.toMutableList()

            _gameState.update { it.copy(enemies = movedEnemies.toList()) }
            delay((150 * delayScale).toLong())

            // -------------------------------------------------------------
            // PHASE 3: CASTLE DAMAGE & BREACH CHECK
            // -------------------------------------------------------------
            var currentCastleHp = _gameState.value.castleHp
            val enemiesStillAlive = mutableListOf<EnemyUnit>()

            for (enemy in movedEnemies) {
                if (enemy.position.row >= GameRules.GRID_ROWS - 1) {
                    // Enemy breached castle threshold (row 5)
                    val damage = enemy.type.castleDamage
                    currentCastleHp = (currentCastleHp - damage).coerceAtLeast(0)

                    val damageEvent = CastleDamageEvent(
                        damage = damage,
                        enemyType = enemy.type,
                        col = enemy.position.col
                    )

                    _gameState.update {
                        it.copy(
                            castleHp = currentCastleHp,
                            activeCastleDamage = damageEvent,
                            statusMessage = "Castle breached by ${enemy.type.title}! -$damage HP"
                        )
                    }

                    if (settings.soundEnabled) soundSynthesizer.playCastleHit()
                    hapticsHelper.vibrateCastleDamage(settings.hapticsEnabled)

                    delay((240 * delayScale).toLong())
                    _gameState.update { it.copy(activeCastleDamage = null) }
                } else {
                    enemiesStillAlive.add(enemy)
                }
            }

            _gameState.update { it.copy(enemies = enemiesStillAlive.toList()) }

            // -------------------------------------------------------------
            // PHASE 4: LOSS CHECK
            // -------------------------------------------------------------
            if (currentCastleHp <= 0) {
                val finalEnemiesDefeated = _gameState.value.enemiesDefeated
                _gameState.update {
                    it.copy(
                        castleHp = 0,
                        isDefeat = true,
                        isAnimating = false,
                        endTimeMillis = System.currentTimeMillis(),
                        statusMessage = "Defeat! The castle has fallen."
                    )
                }
                if (settings.soundEnabled) soundSynthesizer.playDefeat()
                hapticsHelper.vibrateDefeat(settings.hapticsEnabled)
                repository.recordGameResult(isVictory = false, castleHp = 0, enemiesDefeated = finalEnemiesDefeated)
                return@launch
            }

            // -------------------------------------------------------------
            // PHASE 5: WAVE PROGRESSION & SPAWNING
            // -------------------------------------------------------------
            var currentWave = _gameState.value.wave
            val spawnQueue = _gameState.value.spawnQueue.toMutableList()

            // If current wave board is clear and spawnQueue is empty
            if (enemiesStillAlive.isEmpty() && spawnQueue.isEmpty()) {
                if (currentWave >= GameRules.MAX_WAVES) {
                    // VICTORY!
                    val finalEnemiesDefeated = _gameState.value.enemiesDefeated
                    _gameState.update {
                        it.copy(
                            isVictory = true,
                            isAnimating = false,
                            endTimeMillis = System.currentTimeMillis(),
                            statusMessage = "Victory! All 5 waves defeated!"
                        )
                    }
                    if (settings.soundEnabled) soundSynthesizer.playVictory()
                    hapticsHelper.vibrateVictory(settings.hapticsEnabled)
                    repository.recordGameResult(isVictory = true, castleHp = currentCastleHp, enemiesDefeated = finalEnemiesDefeated)
                    return@launch
                } else {
                    // Advance to next wave
                    currentWave++
                    val newWaveEnemies = GameRules.getWaveEnemies(currentWave).toMutableList()
                    spawnQueue.clear()
                    spawnQueue.addAll(newWaveEnemies)
                    _gameState.update {
                        it.copy(
                            wave = currentWave,
                            spawnQueue = spawnQueue.toList(),
                            statusMessage = "Wave $currentWave has arrived!"
                        )
                    }
                    delay((200 * delayScale).toLong())
                }
            }

            // Spawn queued enemies on row 0 in vacant entry columns [1, 3, 5]
            val currentEnemiesOnBoard = _gameState.value.enemies.toMutableList()
            for (col in GameRules.ENTRY_COLUMNS) {
                val isSlotFree = currentEnemiesOnBoard.none { it.position == GridPosition(0, col) }
                if (isSlotFree && spawnQueue.isNotEmpty()) {
                    val nextType = spawnQueue.removeAt(0)
                    currentEnemiesOnBoard.add(
                        EnemyUnit(
                            id = nextEnemyId++,
                            type = nextType,
                            position = GridPosition(0, col),
                            hp = nextType.initialHp,
                            maxHp = nextType.initialHp
                        )
                    )
                }
            }

            // -------------------------------------------------------------
            // PHASE 6: TURN REPLENISHMENT & READY FOR PLAYER
            // -------------------------------------------------------------
            val updatedEnergy = (_gameState.value.energy + GameRules.TURN_ENERGY_GAIN)
                .coerceAtMost(GameRules.MAX_ENERGY)

            _gameState.update { state ->
                state.copy(
                    enemies = currentEnemiesOnBoard.toList(),
                    spawnQueue = spawnQueue.toList(),
                    wave = currentWave,
                    energy = updatedEnergy,
                    turnCount = state.turnCount + 1,
                    isPlayerActionAvailable = true,
                    isAnimating = false,
                    statusMessage = "Turn ${state.turnCount + 1}: +2⚡ energy gained. Choose your action."
                )
            }
        }
    }
}
