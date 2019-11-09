package de.lostmekka._3m5gamejam6.entity

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.GameTileRepository
import de.lostmekka._3m5gamejam6.entity.behavior.InputReceiver
import de.lostmekka._3m5gamejam6.entity.system.Movable
import de.lostmekka._3m5gamejam6.entity.facet.TorchHandling
import de.lostmekka._3m5gamejam6.entity.attribute.EntityHealth
import de.lostmekka._3m5gamejam6.entity.attribute.EntityInventory
import de.lostmekka._3m5gamejam6.entity.attribute.EntityPosition
import de.lostmekka._3m5gamejam6.entity.attribute.EntityTile
import org.hexworks.amethyst.api.Entities
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType

private fun <T : EntityType> newGameEntityOfType(
    type: T,
    init: EntityBuilder<T, GameContext>.() -> Unit
) = Entities.newEntityOfType(type, init)

object EntityFactory {
    fun newPlayer() = newGameEntityOfType(Player) {
        attributes(
            EntityPosition(),
            EntityTile(GameTileRepository.PLAYER),
            EntityHealth(),
            EntityInventory()
        )
        behaviors(InputReceiver)
        facets(Movable, TorchHandling)
    }

    fun newTorchItem() = newGameEntityOfType(TorchItem) {
        attributes(
            EntityPosition(),
            EntityTile(GameTileRepository.TORCH_ITEM)
        )
    }

    fun newTorch() = newGameEntityOfType(Torch) {
        attributes(
            EntityPosition(),
            EntityTile(GameTileRepository.TORCH)
        )
    }
}
