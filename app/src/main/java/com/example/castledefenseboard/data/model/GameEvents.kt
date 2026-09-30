package com.example.castledefenseboard.data.model

import java.util.UUID

data class AttackEvent(
    val id: String = UUID.randomUUID().toString(),
    val defenderId: Int,
    val defenderPos: GridPosition,
    val targetEnemyId: Int,
    val targetPos: GridPosition,
    val damage: Int,
    val type: DefenderType,
    val enemyKilled: Boolean
)

data class CastleDamageEvent(
    val id: String = UUID.randomUUID().toString(),
    val damage: Int,
    val enemyType: EnemyType,
    val col: Int
)

data class DeploymentFeedback(
    val id: String = UUID.randomUUID().toString(),
    val message: String
)
