package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.Rect
import de.lostmekka._3m5gamejam6.entity.EntityFactory
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.splitHorizontal
import de.lostmekka._3m5gamejam6.splitVertical
import de.lostmekka._3m5gamejam6.touchesBorder
import org.hexworks.zircon.api.data.impl.Position3D
import kotlin.random.Random

fun World.generateRooms() {
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
        if (rect.x > 0) gameArea.setBlockAt(
            Position3D.create(rect.x, y, 0),
            GameBlock.door()
        )
        if (rect.y > 0) gameArea.setBlockAt(
            Position3D.create(x, rect.y, 0),
            GameBlock.door()
        )
    }
}

fun World.placeTorch(pos: Position3D) {
    val newTorch = EntityFactory.newTorch()
    gameArea.fetchBlockOrDefault(pos).currentEntities += newTorch
    newTorch.position = pos
    engine.addEntity(newTorch)
    updateLighting()
}

fun World.placeTorchItem(pos: Position3D) {
    val newTorch = EntityFactory.newTorchItem()
    gameArea.fetchBlockOrDefault(pos).currentEntities += newTorch
    newTorch.position = pos
    engine.addEntity(newTorch)
}

fun World.placePlayer() {
    val positionPlayerStart = Position3D.create(10, 10, 0)
    gameArea.fetchBlockOrDefault(positionPlayerStart).currentEntities += player
    player.position = positionPlayerStart
    engine.addEntity(player)
}

fun World.generateMadness() {
    repeat(5) {
        val x = Random.nextInt(gameArea.actualSize().xLength - 1)
        val y = Random.nextInt(gameArea.actualSize().yLength - 1)
        gameArea.fetchBlockAt(Position3D.create(x, y, 0)).get().hasMadness = true
    }
}

