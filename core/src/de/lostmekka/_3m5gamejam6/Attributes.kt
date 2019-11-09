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

fun <T : EntityType> newGameEntityOfType(type: T, init: EntityBuilder<T, GameContext>.() -> Unit) =
    Entities.newEntityOfType(type, init)

object EntityFactory {

    fun newPlayer() = newGameEntityOfType(Player) {
        attributes(EntityPosition(), EntityTile(GameTileRepository.PLAYER))
        behaviors()
        facets()
    }
}

data class EntityTile(val tile: Tile = Tiles.empty()) : Attribute