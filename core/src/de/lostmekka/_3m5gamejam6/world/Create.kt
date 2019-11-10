package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.ActivatedAltar
import de.lostmekka._3m5gamejam6.entity.EntityFactory
import de.lostmekka._3m5gamejam6.entity.OpenedStairs
import de.lostmekka._3m5gamejam6.entity.Player
import de.lostmekka._3m5gamejam6.entity.attribute.position
import org.hexworks.zircon.api.data.impl.Position3D
import kotlin.random.Random

fun World.generateRooms() {
    // fill all with floor, border with walls
    val (w, h) = gameArea.actualSize().to2DSize()
    for (x in 0 until w) {
        for (y in 0 until h) {
            val block = if (x == 0 || y == 0 || x == w-1 || y == h-1) GameBlock.wall() else GameBlock.floor()
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
            gameArea.setBlockAt(position, GameBlock.wall())
            val succ1 = generateRandomWalls(_x = x, _y = y, xDir = !xDir, increase = increase * (-1))
            val succ2 = generateRandomWalls(_x = x, _y = y, xDir = !xDir, increase = increase)
            if (!(succ1 && succ2)) {
                gameArea.setBlockAt(Position3D.create(if (xDir) x + increase else x, if (xDir) y else y + increase, 0), GameBlock.wall())
            }
            return true
        }
    } else {
        gameArea.setBlockAt(position, GameBlock.wall())
        generateRandomWalls(_x = x, _y = y, prop = prop * 2, increase = increase, xDir = xDir, doorNext = doorNext)
    }
    return true
}

private fun World.checkBlockForFloor(position: Position3D) =
    !gameArea.hasBlockAt(position) || !gameArea.fetchBlockAt(position).get().isWalkable

fun World.placeTorch(pos: Position3D): Boolean {
    val block = this[pos] ?: return false
    if (block.isAltar || block.currentEntities.any { it.type !is Player }) return false
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

fun World.activateAltar(pos: Position3D): Boolean {
    val block = this[pos] ?: return false
    if (!block.isAltar || block.currentEntities.any { it.type !is Player }) return false
    ActivatedAltar.create().also {
        it.position = pos
        block.currentEntities += it
        engine.addEntity(it)
    }
    activatedAltarCount++

    if (activatedAltarCount >= altarCount) {
        val stairsBlock = this[stairsPosition]!!
        OpenedStairs.create().also {
            it.position = stairsPosition
            stairsBlock.currentEntities += it
            engine.addEntity(it)
        }
    }

    return true
}

fun World.generateEnemies() {
    repeat(GameConfig.areaInitialEnemyZombieCount) {
        val x = Random.nextInt(gameArea.actualSize().xLength - 1)
        val y = Random.nextInt(gameArea.actualSize().yLength - 1)
        val pos = Position3D.create(x, y, 0)
        placeEnemyZombie(pos)
    }
}

fun World.placeEnemyZombie(pos: Position3D) {
    val block = this[pos]
    if (block != null && block.isWalkable && block.isTransparent) {
        val newZombie = EntityFactory.newEnemyZombie()
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

fun World.placeStairs() {
    val (pos, _) = fetchSpawnableBlocks().random()
    stairsPosition = pos
    this[pos] = GameBlock.stairs()
}

fun World.generateMadness() {
    gameArea.fetchBlocks()
        .shuffled()
        .take(5)
        .forEach { it.block.hasMadness = true }
}

fun World.generateTorchItems() {
    for ((pos, block) in fetchRandomSpawnableBlocks(GameConfig.areaInitialTorchCount)) {
        val newTorch = EntityFactory.newTorchItem()
        block.currentEntities += newTorch
        engine.addEntity(newTorch)
        newTorch.position = pos
    }
}

fun World.generateAltars() {
    for ((pos, _) in fetchRandomSpawnableBlocks(GameConfig.areaAltarCount)) {
        this[pos] = GameBlock.altar()
        altarCount++
    }
}

