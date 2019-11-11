package de.lostmekka._3m5gamejam6.entity

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.GameTileRepository
import de.lostmekka._3m5gamejam6.entity.attribute.EntityPosition
import de.lostmekka._3m5gamejam6.entity.attribute.EntityTileAnimation
import org.hexworks.amethyst.api.Entities
import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType

private fun <T : EntityType> newGameEntityOfType(
    type: T,
    init: EntityBuilder<T, GameContext>.() -> Unit
) = Entities.newEntityOfType(type, init)

object Player : BaseEntityType("Player")
object EnemyZombie : BaseEntityType("Zombiez")
object TorchItem : BaseEntityType("Torch Item")
object Torch : BaseEntityType("Torch")

object ActivatedAltar : BaseEntityType("Activated Altar") {
    fun create() = newGameEntityOfType(ActivatedAltar) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(GameTileRepository.altarActivated)
        )
    }
}

object OpenedPortal : BaseEntityType("Opened Portal") {
    fun create() = newGameEntityOfType(OpenedPortal) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(GameTileRepository.portalActivated)
        )
    }
}
