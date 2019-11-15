package de.lostmekka._3m5gamejam6.entity.attribute

import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import org.hexworks.amethyst.api.Attribute
import org.hexworks.zircon.api.data.Tile

data class EntityTileAnimation(
    val frames: List<Tile>,
    var currentIndex: Int = 0,
    val madnessFrames: List<Tile>? = null,
    var currentMadnessIndex: Int = 0
) : Attribute {
    constructor(
        tile: Tile,
        madnessTile: Tile? = null
    ) : this(
        frames = listOf(tile),
        madnessFrames = madnessTile?.let { listOf(it) }
    )

    init {
        currentIndex %= frames.size
        if (madnessFrames != null) currentMadnessIndex %= madnessFrames.size
    }

    fun tick() {
        currentIndex = (currentIndex + 1) % frames.size
        if (madnessFrames != null) currentMadnessIndex = (currentMadnessIndex + 1) % madnessFrames.size
    }
}

val AnyGameEntity.tileAnimation: EntityTileAnimation
    get() = tryToFindAttribute(EntityTileAnimation::class)

val AnyGameEntity.tile: Tile
    get() = tileAnimation.run { frames[currentIndex] }

val AnyGameEntity.madnessTile: Tile?
    get() = tileAnimation.run { madnessFrames?.get(currentMadnessIndex) }
