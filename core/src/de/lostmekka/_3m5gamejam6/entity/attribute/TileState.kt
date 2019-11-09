package de.lostmekka._3m5gamejam6.entity.attribute

import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import org.hexworks.amethyst.api.Attribute
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.Tile

data class EntityTile(var tile: Tile = Tiles.empty()) : Attribute

var AnyGameEntity.tile: Tile
    get() = tryToFindAttribute(EntityTile::class).tile
    set(value) {
        tryToFindAttribute(EntityTile::class).tile = value
    }
