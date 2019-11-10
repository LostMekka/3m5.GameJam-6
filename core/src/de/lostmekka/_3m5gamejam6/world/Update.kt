package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.ActivatedAltar
import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.entity.attribute.tileAnimation
import de.lostmekka._3m5gamejam6.nextBoolean
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
    val blocks = gameArea.fetchBlocks()

    // http://dev.theomader.com/gaussian-kernel-calculator/
    val kernel = listOf(0.06136, 0.24477, 0.38774, 0.24477, 0.06136)
    val kernelOffset = kernel.size / 2
    val averageBuffer = mutableMapOf<Position3D, Double>()
    for ((pos, _) in blocks) {
        var average = 0.0
        kernel.forEachIndexed { i, kernelValue ->
            val localValue = if (hasMadnessAt(pos.withRelativeX(i - kernelOffset))) 1.0 else 0.0
            average += localValue * kernelValue
        }
        averageBuffer[pos] = average
    }
    for ((pos, block) in blocks) {
        var average = averageBuffer[pos] ?: 0.0
        kernel.forEachIndexed { i, kernelValue ->
            val localValue = if (hasMadnessAt(pos.withRelativeY(i - kernelOffset))) 1.0 else 0.0
            average += localValue * kernelValue
        }
        block.averageSurroundingMadness = average
    }

    for ((pos, block) in blocks) {
        when {
            block.hasMadness && block.isLit -> {
                val baseProbability = GameConfig.madnessRetreatProbability
                val probability = baseProbability * (1.0 - 0.5 * block.averageSurroundingMadness)
                if (Random.nextBoolean(probability)) {
                    block.hasMadness = false
                }
            }
            !block.hasMadness -> {
                val baseProbability = GameConfig.madnessGrowthProbability
                val probability = baseProbability * (1.0 - 0.5 * block.averageSurroundingMadness)
                if (Random.nextBoolean(probability) && hasMadnessNeighbor(pos)) {
                    block.hasMadness = true
                }
            }
        }
    }
}

private fun World.hasMadnessNeighbor(position: Position3D): Boolean {
    for (x in position.x - 1..position.x + 1) {
        for (y in position.y - 1..position.y + 1) {
            if (hasMadnessAt(Position.create(x, y))) return true
        }
    }
    return false
}

fun World.updateLighting() {
    val torches = mutableListOf<AnyGameEntity>()
    val altars = mutableListOf<AnyGameEntity>()
    for ((_, block) in gameArea.fetchBlocks()) {
        block.isLit = false
        torches += block.currentEntities.filter { it.type is Torch }
        altars += block.currentEntities.filter { it.type is ActivatedAltar }
    }

    // light around player if not holding sword and torches available
    if (!player.inventory.holdsSword && player.inventory.torches > 0) {
        floodLight(player.position.to2DPosition(), GameConfig.torchLightRadius)
    } else {
        this[player.position.to2DPosition()]?.isLit = true
    }

    torches.forEach { floodLight(it.position.to2DPosition(), GameConfig.torchLightRadius) }

    altars.forEach { floodLight(it.position.to2DPosition(), GameConfig.altarLightRadius) }
}

private fun World.floodLight(pos: Position, radius: Int) {
    findVisiblePositionsFor(pos, radius).forEach { this[it]?.isLit = true }
}
