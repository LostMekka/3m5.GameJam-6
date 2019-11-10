package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.GameTileRepository
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.ActivatedAltar
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
    val isAltar: Boolean = false,
    val isStairs: Boolean = false,
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

    val name: String
        get() {
            if (!isLit && GameConfig.fogOfWarEnabled) return "Unknown"
            val name = currentEntities.firstOrNull()?.name ?: tileName
            val modifier = when {
                hasMadness -> " (Madness)"
                else -> ""
            }
            return name + modifier
        }

    var isLit = false
    var averageSurroundingMadness = 0.0
    val altarIsActive get() = currentEntities.any { it.type is ActivatedAltar }

    override fun fetchSide(side: BlockSide): Tile {
        return GameTileRepository.empty
    }

    companion object {
        fun floor1() = GameBlock(
            tile = GameTileRepository.floor1,
            madnessTile = GameTileRepository.floorMadness,
            tileName = "Floor",
            isWalkable = true,
            isTransparent = true
        )
        fun floor2() = GameBlock(
            tile = GameTileRepository.floor2,
            madnessTile = GameTileRepository.floorMadness,
            tileName = "Floor",
            isWalkable = true,
            isTransparent = true
        )


        fun wall1() = GameBlock(
            tile = GameTileRepository.wall1,
            madnessTile = GameTileRepository.wallMadness,
            tileName = "Wall",
            isWalkable = false,
            isTransparent = false
        )

        fun wall2() = GameBlock(
            tile = GameTileRepository.wall2,
            madnessTile = GameTileRepository.wallMadness,
            tileName = "Wall",
            isWalkable = false,
            isTransparent = false
        )

        fun door() = GameBlock(
            tile = GameTileRepository.door,
            madnessTile = GameTileRepository.doorMadness,
            tileName = "Door",
            isWalkable = true,
            isTransparent = false
        )

        fun altar() = GameBlock(
            tile = GameTileRepository.altar,
            madnessTile = GameTileRepository.altarMadness,
            tileName = "Altar",
            isWalkable = true,
            isTransparent = false,
            isAltar = true
        )

        fun stairs() = GameBlock(
            tile = GameTileRepository.stairs,
            madnessTile = GameTileRepository.stairsMadness,
            tileName = "Sealed Stairs",
            isWalkable = true,
            isTransparent = false,
            isStairs = true
        )
    }
}
