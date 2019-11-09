package de.lostmekka._3m5gamejam6

import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.impl.Position3D

/**
 * Specializes [Command] with our [GameContext] type.
 */
typealias GameCommand<T> = Command<T, GameContext>

data class MoveTo(override val context: GameContext,
                  override val source: GameEntity<EntityType>,
                  val position: Position3D) : GameCommand<EntityType>