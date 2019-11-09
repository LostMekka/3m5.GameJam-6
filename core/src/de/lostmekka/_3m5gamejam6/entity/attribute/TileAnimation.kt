package de.lostmekka._3m5gamejam6.entity.attribute

import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import org.hexworks.amethyst.api.Attribute
import org.hexworks.zircon.api.data.Tile

data class EntityTileAnimation(
    val frames: List<Tile>,
    var currentIndex: Int = 0
) : Attribute {
    constructor(tile: Tile): this(listOf(tile))

    init {
        currentIndex %= frames.size
    }

    fun tick() {
        currentIndex = (currentIndex + 1) % frames.size
    }
}

val AnyGameEntity.tileAnimation: EntityTileAnimation
    get() = tryToFindAttribute(EntityTileAnimation::class)

val AnyGameEntity.tile: Tile
    get() = tileAnimation.run { frames[currentIndex] }
