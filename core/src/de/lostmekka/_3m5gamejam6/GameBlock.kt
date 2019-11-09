package de.lostmekka._3m5gamejam6

import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.BlockSide
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BlockBase

class GameBlock(
    private val tile: Tile,
    val tileName: String,
    val isWalkable: Boolean,
    val currentEntities: MutableList<GameEntity<EntityType>> = mutableListOf()
) : BlockBase<Tile>() {
    override val layers: MutableList<Tile>
        get() {
            val entityTiles = currentEntities.map { it.tile }
            val tile = when {
                entityTiles.contains(GameTileRepository.PLAYER) -> GameTileRepository.PLAYER
                entityTiles.isNotEmpty() -> entityTiles.first()
                else -> tile
            }
            return mutableListOf(tile)
        }

    val name get() = currentEntities.firstOrNull()?.name ?: tileName

    override fun fetchSide(side: BlockSide): Tile {
        return GameTileRepository.EMPTY
    }

    companion object {
        fun floor() = GameBlock(GameTileRepository.FLOOR, "Floor", true)
        fun wall() = GameBlock(GameTileRepository.WALL, "Wall", true)
    }
}
