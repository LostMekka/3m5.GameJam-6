package de.lostmekka._3m5gamejam6

import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.Engines
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.data.impl.Size3D
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.shape.EllipseFactory
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.uievent.UIEvent
import kotlin.random.Random

val madnessPropability = 0.05
val torchspawnPropability = 0.01

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

    operator fun get(pos: Position) = this[pos.to3DPosition()]

    operator fun get(pos: Position3D) = gameArea
        .fetchBlockAt(pos)
        .let { if (it.isPresent) it.get() else null }

    operator fun set(pos: Position, block: GameBlock) {
        this[pos.to3DPosition()] = block
    }

    operator fun set(pos: Position3D, block: GameBlock) {
        gameArea.setBlockAt(pos, block)
    }

    private fun checkTorch(pos: Position3D) {
        this.gameArea.fetchBlockOrDefault(pos).currentEntities.filter { it.type is TorchItem }.forEach {
            gameArea.fetchBlockOrDefault(pos).currentEntities -= it
            engine.removeEntity(it)
            //Increase Player Torches
            player.inventory.Torches += 1
        }

    }

    fun moveEntity(entity: GameEntity<EntityType>, position: Position3D): Boolean {
        var success = false
        val oldBlock = gameArea.fetchBlockAt(entity.position)
        val newBlock = gameArea.fetchBlockAt(position)

        if (bothBlocksPresentAndWalkable(oldBlock, newBlock)) {
            // walk
            success = true
            oldBlock.get().currentEntities -= entity
            entity.position = position
            newBlock.get().currentEntities += entity
            checkMadness(newBlock.get())
            checkTorch(position)
        }


        // spread madness
        for (block in gameArea.fetchBlocks()) {
            if (block.block.hasMadness) {
                if (Random.nextDouble() < madnessPropability) {
                    val availablePositions = getGoodNeighbors(block.position)
                    if (availablePositions.isEmpty()) continue
                    gameArea.fetchBlockAt(availablePositions.shuffled()[0]).get().hasMadness = true
                }
            }
        }

        return success
    }

    private fun getGoodNeighbors(position: Position3D): List<Position3D> {
        val goodNeighbors = mutableListOf<Position3D>()
        for (x in position.x - 1..position.x + 1) {
            for (y in position.y - 1..position.y + 1) {
                val neighbor = Position3D.create(x, y, 0)
                if (gameArea.fetchBlockAt(neighbor).isPresent) {
                    if (!gameArea.fetchBlockAt(neighbor).get().hasMadness) goodNeighbors += neighbor
                }
            }
        }

        return goodNeighbors
    }

    private fun checkMadness(block: GameBlock) {
        if (block.hasMadness) {
            // TODO: decrease health of player
        }
    }

    fun update(screen: Screen, uiEvent: UIEvent) {
        torchGenerator()
        engine.update(
            GameContext(
                world = this,
                screen = screen,
                uiEvent = uiEvent,
                player = player
            )
        )

        updateLighting()
    }

    private fun bothBlocksPresentAndWalkable(oldBlock: Maybe<GameBlock>, newBlock: Maybe<GameBlock>) =
        oldBlock.isPresent && newBlock.isPresent && newBlock.get().isWalkable

    fun generateRooms() {
        val (w, h) = gameArea.actualSize().to2DSize()
        val rects = mutableListOf(Rect(0, 0, w - 1, h - 1))
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

        // create doors
        for (rect in rects) {
            val x = rect.x + Random.nextInt(1, rect.w - 1)
            val y = rect.y + Random.nextInt(1, rect.h - 1)
            if (rect.x > 0) gameArea.setBlockAt(Position3D.create(rect.x, y, 0), GameBlock.door())
            if (rect.y > 0) gameArea.setBlockAt(Position3D.create(x, rect.y, 0), GameBlock.door())
        }
    }

    private fun GetRandomPos(): Position3D {
        val x = Random.nextInt(0, GameConfig.windowWidth - GameConfig.sidebarWidth)
        val y = Random.nextInt(0, GameConfig.windowHeight)
        var pos: Position3D = Position3D.create(x, y, 0)
        return pos
    }


    fun torchGenerator() {
        if (Random.nextFloat() <= torchspawnPropability) {
            placeTorch(GetRandomPos())
        }
    }


    fun placeTorch(pos: Position3D) {
        val newTorch = EntityFactory.newTorch()
        gameArea.fetchBlockOrDefault(pos).currentEntities += newTorch
        newTorch.position = pos
        engine.addEntity(newTorch)
    }


    fun placePlayer() {
        val positionPlayerStart = Position3D.create(10, 10, 0)
        gameArea.fetchBlockOrDefault(positionPlayerStart).currentEntities += player
        player.position = positionPlayerStart
        engine.addEntity(player)
    }

    fun generateMadness() {
        repeat(5) {
            val x = Random.nextInt(gameArea.actualSize().xLength - 1)
            val y = Random.nextInt(gameArea.actualSize().yLength - 1)
            gameArea.fetchBlockAt(Position3D.create(x, y, 0)).get().hasMadness = true
        }
    }

    fun updateLighting() {
        val torches = mutableListOf<AnyGameEntity>()
        for ((_, block) in gameArea.fetchBlocks()) {
            block.isLit = false
            // TODO
//            torches += block.currentEntities.filter { it.type is Torch }
        }
        floodLight(player.position.to2DPosition(), GameConfig.playerLightRadius)
        torches.forEach { floodLight(it.position.to2DPosition(), GameConfig.torchLightRadius) }
    }

    fun floodLight(pos: Position, radius: Int) {
        findVisiblePositionsFor(pos, radius).forEach { this[it]?.isLit = true }
    }

    fun findVisiblePositionsFor(pos: Position, radius: Int): Iterable<Position> {
        val boundary = EllipseFactory.buildEllipse(
            fromPosition = pos,
            toPosition = pos.withRelativeX(radius).withRelativeY(radius)
        )
        return boundary
            .positions()
            .flatMap { ringPos ->
                val result = mutableSetOf<Position>()
                val line1 = LineFactory.buildLine(pos, ringPos).toList()
                // line2 is necessary since there are direction dependent rounding errors that produce unwanted blind spots
                val line2 = line1.map { Position.create(it.x, 2 * pos.y - it.y) }
                val n1 = line1.takeWhile { it == pos || this[it]?.isTransparent == true }.size
                val n2 = line2.takeWhile { it == pos || this[it]?.isTransparent == true }.size
                result += line1.take(n1+1)
                result += line2.take(n2+1)
                result
            }
    }
}

private data class Rect(val x: Int, val y: Int, val w: Int, val h: Int)

private fun Rect.contains(x: Int, y: Int) =
    x in ((this.x + 1) until (this.x + w)) && y in ((this.y + 1) until (this.y + h))

private fun Rect.touches(x: Int, y: Int) =
    x in (this.x..(this.x + w)) && y in (this.y..(this.y + h))

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
}
