package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.GameTileRepository
import de.lostmekka._3m5gamejam6.config.gameConfig
import de.lostmekka._3m5gamejam6.entity.ActivatedAltar
import de.lostmekka._3m5gamejam6.entity.Zombie
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.OpenedPortal
import de.lostmekka._3m5gamejam6.entity.Player
import de.lostmekka._3m5gamejam6.entity.attribute.madnessTile
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
    val isDoor: Boolean = false,
    val isAltar: Boolean = false,
    val isPortal: Boolean = false,
    val currentEntities: MutableList<GameEntity<EntityType>> = mutableListOf()
) : BlockBase<Tile>() {
    override val layers: MutableList<Tile>
        get() {
            if (!isLit && !gameConfig.debug.seeEverything) return mutableListOf(GameTileRepository.shadow)

            val entity = findEntity<Player>()
                ?: findEntity<Zombie>()
                ?: firstEntity()
            val tile = when {
                hasMadness -> entity?.madnessTile ?: madnessTile
                else -> entity?.tile ?: tile
            }
            return mutableListOf(tile)
        }

    val name: String
        get() {
            if (!isLit && !gameConfig.debug.seeEverything) return "Unknown"
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
    val portalIsOpen get() = currentEntities.any { it.type is OpenedPortal }

    override fun fetchSide(side: BlockSide): Tile {
        return GameTileRepository.empty
    }

    inline fun <reified T : EntityType> findEntity() = currentEntities.find { it.type is T }
    fun firstEntity() = currentEntities.firstOrNull()

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
            isTransparent = false,
            isDoor = true
        )

        fun altar() = GameBlock(
            tile = GameTileRepository.altar,
            madnessTile = GameTileRepository.altarMadness,
            tileName = "Altar",
            isWalkable = true,
            isTransparent = false,
            isAltar = true
        )

        fun portal() = GameBlock(
            tile = GameTileRepository.portal,
            madnessTile = GameTileRepository.portalMadness,
            tileName = "Sealed Portal",
            isWalkable = true,
            isTransparent = false,
            isPortal = true
        )
    }
}
