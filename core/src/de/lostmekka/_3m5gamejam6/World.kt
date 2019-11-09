package de.lostmekka._3m5gamejam6

import com.badlogic.gdx.Game
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.Engines
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.datatypes.extensions.map
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.data.impl.Size3D
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.UIEvent

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

    private val engine: Engine<GameContext> = Engines.newEngine()

    init {
        for (position in visibleSize.fetchPositions()) {
            val block = GameBlock.floor()
            gameArea.setBlockAt(position,block)
            block.currentEntities.forEach { entity ->
                engine.addEntity(entity)
                entity.position = position
            }
        }
        val positionPlayerStart = Position3D.create(10, 10, 0)
        gameArea.fetchBlockOrDefault(positionPlayerStart).currentEntities += player
        player.position = positionPlayerStart
        engine.addEntity(player)
    }

    fun moveEntity(entity: GameEntity<EntityType>, position: Position3D): Boolean {
        var success = false
        val oldBlock = gameArea.fetchBlockAt(entity.position)
        val newBlock = gameArea.fetchBlockAt(position)

        if (bothBlocksPresent(oldBlock, newBlock)) {
            success = true
            oldBlock.get().currentEntities -= entity
            entity.position = position
            newBlock.get().currentEntities += entity
        }
        return success
    }

    fun update(screen: Screen, uiEvent: UIEvent) {
        engine.update(
            GameContext(
                world = this,
                screen = screen,
                uiEvent = uiEvent,
                player = player
            )
        )
    }

    private fun bothBlocksPresent(oldBlock: Maybe<GameBlock>, newBlock: Maybe<GameBlock>) =  // 7
        oldBlock.isPresent && newBlock.isPresent
}
