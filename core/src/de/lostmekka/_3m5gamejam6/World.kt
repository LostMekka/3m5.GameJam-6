package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.data.impl.Size3D
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

    init {
        val positionPlayerStart = Position3D.create(10, 10, 0)
        gameArea.setBlockAt(positionPlayerStart, GameBlock.floor())
        gameArea.fetchBlockOrDefault(positionPlayerStart).currentEntities += player
    }

    fun generateRooms() {
        val (w, h) = gameArea.actualSize().to2DSize()
        val rects = mutableListOf(Rect(0, 0, w-1, h-1))
        repeat(5) {
            val i = rects.indices.random()
            val rect = rects.removeAt(i)
            rects += if (Random.nextBoolean()) {
                rect.splitHorizontal()
            } else {
                rect.splitVertical()
            }
        }
        println(rects)

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
    x in ((this.x + 1) until (this.x + w)) || y in ((this.y + 1) until (this.y + h))

private fun Rect.touches(x: Int, y: Int) =
    x in (this.x .. (this.x + w)) || y in (this.y .. (this.y + h))

private fun Rect.touchesBorder(x: Int, y: Int) = touches(x, y) && !contains(x, y)

private val Rect.area get() = w * h

private fun Rect.splitHorizontal(): List<Rect> {
    if (h <= 6) return listOf(this)
    val pos = 3 + Random.nextInt(h - 3)
    return listOf(Rect(x, y, w, pos), Rect(x, y + pos, w, h - pos))
}

private fun Rect.splitVertical(): List<Rect> {
    if (w <= 6) return listOf(this)
    val pos = 3 + Random.nextInt(w - 3)
    return listOf(Rect(x, y, pos, h), Rect(x + pos, y, w - pos, h))
}
