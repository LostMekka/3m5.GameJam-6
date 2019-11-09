package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.data.impl.Size3D

class World(
    visibleSize: Size3D,
    actualSize: Size3D
) {
    val gameArea = GameAreaBuilder.newBuilder<Tile, GameBlock>()
        .withVisibleSize(visibleSize)
        .withActualSize(actualSize)
        .withDefaultBlock(GameBlock.floor())
        .withLayersPerBlock(1)
        .build()

    val player = EntityFactory.newPlayer()

    init {
        val positionPlayerStart = Position3D.create(10, 10, 0)
        gameArea.setBlockAt(positionPlayerStart, GameBlock.floor())
        gameArea.fetchBlockOrDefault(positionPlayerStart).currentEntities += player
    }
}
