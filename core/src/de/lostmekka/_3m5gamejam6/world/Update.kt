package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.entity.attribute.tileAnimation
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.impl.Position3D
import kotlin.random.Random

fun World.updateTorches() {
    gameArea.fetchBlocks().forEach { (_, block) ->
        block.currentEntities
            .filter { it.type is Torch }
            .forEach { it.tileAnimation.tick() }
    }
}

fun World.updateMadness() {
    for (block in gameArea.fetchBlocks()) {
        if (block.block.hasMadness) {
            if (Random.nextDouble() < GameConfig.madnessProbability) {
                val availablePositions = getGoodNeighbors(block.position)
                if (availablePositions.isEmpty()) continue
                gameArea.fetchBlockAt(availablePositions.shuffled()[0]).get().hasMadness = true
            }
        }
    }
}

private fun World.getGoodNeighbors(position: Position3D): List<Position3D> {
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

fun World.updateLighting() {
    val torches = mutableListOf<AnyGameEntity>()
    for ((_, block) in gameArea.fetchBlocks()) {
        block.isLit = false
        torches += block.currentEntities.filter { it.type is Torch }
    }
    floodLight(player.position.to2DPosition(), GameConfig.playerLightRadius)
    torches.forEach { floodLight(it.position.to2DPosition(), GameConfig.torchLightRadius) }
}

private fun World.floodLight(pos: Position, radius: Int) {
    findVisiblePositionsFor(pos, radius).forEach { this[it]?.isLit = true }
}
