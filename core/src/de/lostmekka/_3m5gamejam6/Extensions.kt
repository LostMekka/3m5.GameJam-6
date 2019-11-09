package de.lostmekka._3m5gamejam6

import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.extensions.map
import org.hexworks.cobalt.datatypes.extensions.orElseThrow
import org.hexworks.zircon.api.data.Tile
import kotlin.reflect.KClass

typealias AnyGameEntity = Entity<EntityType, GameContext>

typealias GameEntity<T> = Entity<T, GameContext>

var AnyGameEntity.position // 1
    get() = tryToFindAttribute(EntityPosition::class).position // 2
    set(value) { // 3
        findAttribute(EntityPosition::class).map {
            it.position = value
        }
    }

var AnyGameEntity.health // 1
    get() = tryToFindAttribute(EntityHealth::class).health // 2
    set(value) { // 3
        findAttribute(EntityHealth::class).map {
            it.health = value
        }
    }


var AnyGameEntity.inventory // 1
    get() = tryToFindAttribute(EntityInventory::class).inventory // 2
    set(value) { // 3
        findAttribute(EntityInventory::class).map {
            it.inventory = value
        }
    }



val AnyGameEntity.tile: Tile
    get() = this.tryToFindAttribute(EntityTile::class).tile

// 4
fun <T : Attribute> AnyGameEntity.tryToFindAttribute(klass: KClass<T>): T = findAttribute(klass).orElseThrow {
    NoSuchElementException("Entity '$this' has no property with type '${klass.simpleName}'.")
}