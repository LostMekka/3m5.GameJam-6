package de.lostmekka._3m5gamejam6

import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.Entities
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.createPropertyFrom
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D

class EntityPosition(initialPosition: Position3D = Position3D.unknown()) : Attribute {
    private val positionProperty = createPropertyFrom(initialPosition)

    var position: Position3D by positionProperty.asDelegate()
}

class EntityHealth(initialHealth: Int = 100) : Attribute {
    private val healthProperty = createPropertyFrom(initialHealth)

    var health: Int by healthProperty.asDelegate()
}

class EntityInventory(initialInventory: Inventory = Inventory()) : Attribute {
    private val inventoryProperty = createPropertyFrom(initialInventory)
    var inventory: Inventory by inventoryProperty.asDelegate()
}



fun <T : EntityType> newGameEntityOfType(type: T, init: EntityBuilder<T, GameContext>.() -> Unit) =
    Entities.newEntityOfType(type, init)

object EntityFactory {

    fun newPlayer() = newGameEntityOfType(Player) {
        attributes(EntityPosition(), EntityTile(GameTileRepository.PLAYER), EntityHealth(), EntityInventory())
        behaviors(InputReceiver)
        facets(Movable)
    }

    fun newTorch() = newGameEntityOfType(TorchItem) {
        attributes(EntityPosition(), EntityTile(GameTileRepository.TORCH))
        /*attributes(isTorch())*/
    }

}

data class EntityTile(var tile: Tile = Tiles.empty()) : Attribute