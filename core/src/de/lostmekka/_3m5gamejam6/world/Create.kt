package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.EntityFactory
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.attribute.position
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.impl.Position3D
import kotlin.random.Random

fun World.generateRooms() {
    val (w, h) = gameArea.actualSize().to2DSize()
    for (x in 0 until w) {
        for (y in 0 until h) {
            gameArea.setBlockAt(Position3D.create(x, y, 0), GameBlock.floor())
        }
    }

    generateRandomWalls(-1, Random.nextInt(h - 1))
}

private fun World.generateRandomWalls(_x: Int, _y: Int, prop: Double = 0.01, doorNext: Boolean = true, xDir: Boolean = true, increase: Int = 1) {
    val x = if (xDir) _x + increase else _x
    val y = if (xDir) _y else _y + increase
    val position = Position3D.create(x, y, 0)
    if (xDir) {
        if (checkBlockForFloor(Position3D.create(x, y - 1, 0))) return
        if (checkBlockForFloor(Position3D.create(x, y - 2, 0))) return
        if (checkBlockForFloor(Position3D.create(x, y + 1, 0))) return
        if (checkBlockForFloor(Position3D.create(x, y + 2, 0))) return
    } else {
        if (checkBlockForFloor(Position3D.create(x - 1, y, 0))) return
        if (checkBlockForFloor(Position3D.create(x - 2, y, 0))) return
        if (checkBlockForFloor(Position3D.create(x + 1, y, 0))) return
        if (checkBlockForFloor(Position3D.create(x + 2, y, 0))) return
    }
    if (checkBlockForFloor(position)) return
    if (Random.nextDouble() < prop) {
        if (doorNext) {
            gameArea.setBlockAt(position, GameBlock.door())
            generateRandomWalls(_x = x, _y = y, doorNext = false, increase = increase, xDir = xDir)
        } else {
            gameArea.setBlockAt(position, GameBlock.wall())
            generateRandomWalls(_x = x, _y = y, xDir = !xDir, increase = increase * (-1))
            generateRandomWalls(_x = x, _y = y, xDir = !xDir, increase = increase)
            return
        }
    } else {
        gameArea.setBlockAt(position, GameBlock.wall())
        generateRandomWalls(_x = x, _y = y, prop = prop * 2, increase = increase, xDir = xDir, doorNext = doorNext)
    }
}

private fun World.checkBlockForFloor(position: Position3D) =
        !gameArea.hasBlockAt(position) || !gameArea.fetchBlockAt(position).get().isWalkable

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

fun World.generateEnemies()
{
    repeat(GameConfig.areaInitialEnemyZombieCount) {
        val x = Random.nextInt(gameArea.actualSize().xLength - 1)
        val y = Random.nextInt(gameArea.actualSize().yLength - 1)
        val pos = Position3D.create(x, y, 0)
        placeEnemyZombie(pos)
    }
}

fun World.placeEnemyZombie(pos: Position3D)
{
    val block = this[pos]
    if (block != null && block.isWalkable && block.isTransparent) {
        val newZombie = EntityFactory.newEnemyZombie()
        newZombie.position = pos
        block.currentEntities += newZombie
        engine.addEntity(newZombie)
    }
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
