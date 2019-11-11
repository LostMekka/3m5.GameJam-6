package de.lostmekka._3m5gamejam6.entity

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.world.World
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.impl.Position3D

data class MoveTo(
    override val context: GameContext,
    override val source: GameEntity<EntityType>,
    val position: Position3D
) : GameCommand<EntityType>

data class AIAction(
    val world: World,
    val entity: GameEntity<EnemyZombie>
)

data class GrabTorchItem(
    override val context: GameContext,
    override val source: GameEntity<EntityType>,
    val position: Position3D
) : GameCommand<EntityType>

data class BuildTorch(
    override val context: GameContext,
    override val source: GameEntity<EntityType>,
    val position: Position3D
) : GameCommand<EntityType>

data class EquipTorch(
    override val context: GameContext,
    override val source: GameEntity<EntityType>
) : GameCommand<EntityType>

data class EquipSword(
    override val context: GameContext,
    override val source: GameEntity<EntityType>
) : GameCommand<EntityType>

data class ActivateAltar(
    override val context: GameContext,
    override val source: GameEntity<EntityType>,
    val position: Position3D
) : GameCommand<EntityType>
