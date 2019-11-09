package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.Rect
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.EntityFactory
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.splitHorizontal
import de.lostmekka._3m5gamejam6.splitVertical
import de.lostmekka._3m5gamejam6.touchesBorder
import org.hexworks.zircon.api.data.Position
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

fun World.placeTorch(pos: Position3D): Boolean {
    val block = this[pos] ?: return false
    if (block.currentEntities.any { it.type is Torch }) return false
    val newTorch = EntityFactory.newTorch()
    newTorch.position = pos
    block.currentEntities += newTorch
    engine.addEntity(newTorch)
    updateLighting()
    return true
}

fun World.placeTorchItem(pos: Position3D) {
    val block = this[pos] ?: return
    val newTorch = EntityFactory.newTorchItem()
    block.currentEntities += newTorch
    engine.addEntity(newTorch)
    newTorch.position = pos
}

fun World.placePlayer() {
    val pos = Position3D.create(10, 10, 0)
    val block = this[pos] ?: return
    player.position = pos
    block.currentEntities += player
    engine.addEntity(player)
}

fun World.generateMadness() {
    repeat(5) {
        val x = Random.nextInt(gameArea.actualSize().xLength - 1)
        val y = Random.nextInt(gameArea.actualSize().yLength - 1)
        this[Position.create(x, y)]?.hasMadness = true
    }
}

fun World.generateTorchItems() {
    repeat(GameConfig.areaInitialTorchCount) {
        val x = Random.nextInt(gameArea.actualSize().xLength - 1)
        val y = Random.nextInt(gameArea.actualSize().yLength - 1)
        val pos = Position3D.create(x, y, 0)
        val block = this[pos]
        if (block != null && block.isWalkable && block.isTransparent) {
            val newTorch = EntityFactory.newTorchItem()
            block.currentEntities += newTorch
            engine.addEntity(newTorch)
            newTorch.position = pos
        }
    }
}
