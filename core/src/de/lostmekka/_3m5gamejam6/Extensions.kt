package de.lostmekka._3m5gamejam6

import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.extensions.map
import org.hexworks.cobalt.datatypes.extensions.orElseThrow
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.Tile
import kotlin.reflect.KClass

typealias AnyGameEntity = Entity<EntityType, GameContext>

typealias GameEntity<T> = Entity<T, GameContext>

var AnyGameEntity.position
    get() = tryToFindAttribute(EntityPosition::class).position
    set(value) {
        findAttribute(EntityPosition::class).map {
            it.position = value
        }
    }

var AnyGameEntity.position2D
    get() = position.to2DPosition()
    set(value) {
        position = value.to3DPosition()
    }

var AnyGameEntity.health
    get() = tryToFindAttribute(EntityHealth::class).health
    set(value) {
        findAttribute(EntityHealth::class).map {
            it.health = value
        }
    }


var AnyGameEntity.inventory
    get() = tryToFindAttribute(EntityInventory::class).inventory
    set(value) {
        findAttribute(EntityInventory::class).map {
            it.inventory = value
        }
    }




var AnyGameEntity.tile: Tile
    get() = tryToFindAttribute(EntityTile::class).tile
    set(value) {
        tryToFindAttribute(EntityTile::class).tile = value
    }
// 4
fun <T : Attribute> AnyGameEntity.tryToFindAttribute(klass: KClass<T>): T = findAttribute(klass).orElseThrow {
    NoSuchElementException("Entity '$this' has no property with type '${klass.simpleName}'.")
}
