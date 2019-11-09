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
import kotlin.random.Random

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
        gameArea.setBlockAt(positionPlayerStart, GameBlock.floor())
        gameArea.fetchBlockOrDefault(positionPlayerStart).currentEntities += player
        player.position = positionPlayerStart
        engine.addEntity(player)
    }

    fun generateRooms() {
        val (w, h) = gameArea.actualSize().to2DSize()
        val rects = mutableListOf(Rect(0, 0, w-1, h-1))
        repeat(40) {
            val i = rects.indices.random()
            val rect = rects.removeAt(i)
            rects += if (Random.nextBoolean()) {
                rect.splitHorizontal()
            } else {
                rect.splitVertical()
            }
        }

        for (x in 0 until w) {
            for (y in 0 until h) {
                val wall = rects.any { it.touchesBorder(x, y) }
                val block = if (wall) GameBlock.wall() else GameBlock.floor()
                gameArea.setBlockAt(Position3D.create(x, y, 0), block)
            }
        }
    }
}

private data class Rect(val x: Int, val y: Int, val w: Int, val h: Int)

private fun Rect.contains(x: Int, y: Int) =
    x in ((this.x + 1) until (this.x + w)) && y in ((this.y + 1) until (this.y + h))

private fun Rect.touches(x: Int, y: Int) =
    x in (this.x .. (this.x + w)) && y in (this.y .. (this.y + h))

private fun Rect.touchesBorder(x: Int, y: Int) = touches(x, y) && !contains(x, y)

private val Rect.area get() = w * h

private fun Rect.splitHorizontal(): List<Rect> {
    if (h <= 6) return listOf(this)
    val pos = 3 + Random.nextInt(h - 6)
    return listOf(Rect(x, y, w, pos), Rect(x, y + pos, w, h - pos))
}

private fun Rect.splitVertical(): List<Rect> {
    if (w <= 6) return listOf(this)
    val pos = 3 + Random.nextInt(w - 6)
    return listOf(Rect(x, y, pos, h), Rect(x + pos, y, w - pos, h))

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
