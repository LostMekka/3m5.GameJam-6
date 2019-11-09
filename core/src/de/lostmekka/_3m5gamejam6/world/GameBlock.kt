package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.GameTileRepository
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.attribute.tile
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.BlockSide
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BlockBase

class GameBlock(
    private val tile: Tile,
    private val madnessTile: Tile,
    val tileName: String,
    val isWalkable: Boolean,
    val isTransparent: Boolean,
    var hasMadness: Boolean = false,
    val currentEntities: MutableList<GameEntity<EntityType>> = mutableListOf()
) : BlockBase<Tile>() {
    override val layers: MutableList<Tile>
        get() {
            if (!isLit && GameConfig.fogOfWarEnabled) return mutableListOf(GameTileRepository.shadow)

            val entityTiles = currentEntities.map { it.tile }
            val tile = when {
                hasMadness && entityTiles.contains(GameTileRepository.player) -> GameTileRepository.playerMadness
                entityTiles.contains(GameTileRepository.player) -> GameTileRepository.player
                entityTiles.isNotEmpty() -> entityTiles.first()
                hasMadness -> madnessTile
                else -> tile
            }
            return mutableListOf(tile)
        }

    val name get() = currentEntities.firstOrNull()?.name ?: tileName

    var isLit = false

    override fun fetchSide(side: BlockSide): Tile {
        return GameTileRepository.empty
    }

    companion object {
        fun floor() = GameBlock(
            GameTileRepository.floor,
            GameTileRepository.floorMadness,
            "Floor",
            true,
            true
        )
        fun wall() = GameBlock(
            GameTileRepository.wall,
            GameTileRepository.wallMadness,
            "Wall",
            false,
            false
        )
        fun door() = GameBlock(
            GameTileRepository.door,
            GameTileRepository.doorMadness,
            "Door",
            true,
            false
        )
    }
}
