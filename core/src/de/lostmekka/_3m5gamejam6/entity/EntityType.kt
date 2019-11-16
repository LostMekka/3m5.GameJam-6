package de.lostmekka._3m5gamejam6.entity

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.GameTileRepository
import de.lostmekka._3m5gamejam6.entity.attribute.EntityHealth
import de.lostmekka._3m5gamejam6.entity.attribute.EntityInventory
import de.lostmekka._3m5gamejam6.entity.attribute.EntityPosition
import de.lostmekka._3m5gamejam6.entity.attribute.EntityTileAnimation
import de.lostmekka._3m5gamejam6.entity.behavior.InputReceiver
import de.lostmekka._3m5gamejam6.entity.facet.Equipable
import de.lostmekka._3m5gamejam6.entity.facet.Movable
import de.lostmekka._3m5gamejam6.entity.facet.TorchHandling
import org.hexworks.amethyst.api.Entities
import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.random.Random

private fun <T : EntityType> newGameEntityOfType(
    type: T,
    init: EntityBuilder<T, GameContext>.() -> Unit
) = Entities.newEntityOfType(type, init)

object Player : BaseEntityType("Player") {
    fun create() = newGameEntityOfType(Player) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                tile = GameTileRepository.player,
                madnessTile = GameTileRepository.playerMadness
            ),
            EntityHealth(),
            EntityInventory()
        )
        behaviors(InputReceiver)
        facets(Movable, TorchHandling, Equipable)
    }
}

object Zombie : BaseEntityType("Zombie") {
    fun create() = newGameEntityOfType(Zombie) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                tile = GameTileRepository.zombie,
                madnessTile = GameTileRepository.zombieMadness
            ),
            EntityHealth()
        )
        facets(Movable)
    }
}

object TorchItem : BaseEntityType("Torch Item") {
    fun create() = newGameEntityOfType(TorchItem) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(GameTileRepository.torchItem)
        )
    }
}

object Torch : BaseEntityType("Torch") {
    fun create(frame: Int = Random.nextInt()) = newGameEntityOfType(Torch) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                frames = GameTileRepository.torch,
                currentIndex = frame,
                madnessFrames = listOf(GameTileRepository.torchMadness)
            )
        )
    }
}

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
