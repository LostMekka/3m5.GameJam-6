package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.config.gameConfig
import de.lostmekka._3m5gamejam6.entity.ActivatedAltar
import de.lostmekka._3m5gamejam6.entity.Zombie
import de.lostmekka._3m5gamejam6.entity.OpenedPortal
import de.lostmekka._3m5gamejam6.entity.Player
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.TorchItem
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.nextBoolean
import org.hexworks.zircon.api.data.impl.Position3D
import kotlin.random.Random

fun World.generateRooms() {
    // fill all with floor, border with walls
    val (w, h) = gameArea.actualSize().to2DSize()
    for (x in 0 until w) {
        for (y in 0 until h) {
            val block = when {
                x == 0 || y == 0 || x == w - 1 || y == h - 1 -> GameBlock.wall1()
                Random.nextBoolean(gameConfig.levelGeneration.alternateFloorChance) -> GameBlock.floor2()
                else -> GameBlock.floor1()
            }
            gameArea.setBlockAt(Position3D.create(x, y, 0), block)
        }
    }

    generateRandomWalls(0, Random.nextInt(1, h - 2))
}

private fun World.generateRandomWalls(
    _x: Int,
    _y: Int,
    prop: Double = 0.01,
    doorNext: Boolean = true,
    xDir: Boolean = true,
    increase: Int = 1
): Boolean {
    val x = if (xDir) _x + increase else _x
    val y = if (xDir) _y else _y + increase
    val position = Position3D.create(x, y, 0)
    if (xDir) {
        if (checkBlockForFloor(Position3D.create(x, y - 1, 0))) return false
        if (checkBlockForFloor(Position3D.create(x, y - 2, 0))) return false
        if (checkBlockForFloor(Position3D.create(x, y + 1, 0))) return false
        if (checkBlockForFloor(Position3D.create(x, y + 2, 0))) return false
    } else {
        if (checkBlockForFloor(Position3D.create(x - 1, y, 0))) return false
        if (checkBlockForFloor(Position3D.create(x - 2, y, 0))) return false
        if (checkBlockForFloor(Position3D.create(x + 1, y, 0))) return false
        if (checkBlockForFloor(Position3D.create(x + 2, y, 0))) return false
    }
    if (checkBlockForFloor(position)) return false
    if (Random.nextDouble() < prop) {
        if (doorNext) {
            gameArea.setBlockAt(position, GameBlock.door())
            generateRandomWalls(_x = x, _y = y, doorNext = false, increase = increase, xDir = xDir)
        } else {
            gameArea.setBlockAt(
                position,
                if (Random.nextBoolean(gameConfig.levelGeneration.alternateWallChance)) GameBlock.wall2() else GameBlock.wall1()
            )
            val succ1 = generateRandomWalls(_x = x, _y = y, xDir = !xDir, increase = increase * (-1))
            val succ2 = generateRandomWalls(_x = x, _y = y, xDir = !xDir, increase = increase)
            if (!(succ1 && succ2)) {
                gameArea.setBlockAt(
                    Position3D.create(if (xDir) x + increase else x, if (xDir) y else y + increase, 0),
                    if (Random.nextBoolean(gameConfig.levelGeneration.alternateWallChance)) GameBlock.wall2() else GameBlock.wall1()
                )
            }
            return true
        }
    } else {
        gameArea.setBlockAt(
            position,
            if (Random.nextBoolean(gameConfig.levelGeneration.alternateWallChance)) GameBlock.wall2() else GameBlock.wall1()
        )
        generateRandomWalls(_x = x, _y = y, prop = prop * 2, increase = increase, xDir = xDir, doorNext = doorNext)
    }
    return true
}

private fun World.checkBlockForFloor(position: Position3D) =
    !gameArea.hasBlockAt(position) || !gameArea.fetchBlockAt(position).get().isWalkable

fun World.placeTorch(pos: Position3D): Boolean {
    val block = this[pos] ?: return false
    if (block.isAltar || block.currentEntities.any { it.type !is Player }) return false
    val newTorch = Torch.create()
    newTorch.position = pos
    block.currentEntities += newTorch
    engine.addEntity(newTorch)
    updateLighting()
    return true
}

fun World.placeTorchItem(pos: Position3D) {
    val block = this[pos] ?: return
    val newTorch = TorchItem.create()
    block.currentEntities += newTorch
    engine.addEntity(newTorch)
    newTorch.position = pos
}

fun World.activateAltar(pos: Position3D): Boolean {
    val block = this[pos] ?: return false
    if (!block.isAltar || block.currentEntities.any { it.type !is Player }) return false
    ActivatedAltar.create().also {
        it.position = pos
        block.currentEntities += it
        engine.addEntity(it)
    }
    activatedAltarCount++
    player.health += gameConfig.player.altarHealthBonus

    if (activatedAltarCount >= altarCount) {
        val portalBlock = this[portalPosition]!!
        OpenedPortal.create().also {
            it.position = portalPosition
            portalBlock.currentEntities += it
            engine.addEntity(it)
        }
    }

    return true
}

fun World.generateEnemies() {
    val count = gameConfig.levelGeneration.zombieCount + levelDepth
    for ((pos, block) in fetchRandomSpawnableBlocks(count)) {
        val newZombie = Zombie.create()
        newZombie.position = pos
        block.currentEntities += newZombie
        engine.addEntity(newZombie)
    }
}

fun World.placePlayer() {
    val (pos, block) = fetchSpawnableBlocks().random()
    player.position = pos
    block.currentEntities += player
    engine.addEntity(player)
}

fun World.placePortal() {
    val (pos, _) = fetchSpawnableBlocks().random()
    portalPosition = pos
    this[pos] = GameBlock.portal()
}

fun World.generateMadness() {
    gameArea.fetchBlocks()
        .shuffled()
        .take(5 + levelDepth)
        .forEach { it.block.hasMadness = true }
}

fun World.generateTorchItems() {
    for ((pos, block) in fetchRandomSpawnableBlocks(gameConfig.levelGeneration.torchCount)) {
        val newTorch = TorchItem.create()
        block.currentEntities += newTorch
        engine.addEntity(newTorch)
        newTorch.position = pos
    }
}

fun World.generateAltars() {
    for ((pos, _) in fetchRandomSpawnableBlocks(gameConfig.levelGeneration.altarCount)) {
        this[pos] = GameBlock.altar()
        altarCount++
    }
}

