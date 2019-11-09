package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.data.BlockSide
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BlockBase

class GameBlock(
    private val tile: Tile,
    val name: String,
    val isWalkable: Boolean
) : BlockBase<Tile>() {
    override val layers get() = mutableListOf(tile)

    override fun fetchSide(side: BlockSide): Tile {
        return GameTileRepository.EMPTY
    }

    companion object {
        fun floor() = GameBlock(GameTileRepository.FLOOR, "Floor",true)
        fun wall() = GameBlock(GameTileRepository.WALL, "Wall",true)
    }
}
