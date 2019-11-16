package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.config.gameConfig
import de.lostmekka._3m5gamejam6.entity.ActivatedAltar
import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import de.lostmekka._3m5gamejam6.entity.Zombie
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.entity.attribute.position2D
import de.lostmekka._3m5gamejam6.entity.attribute.tileAnimation
import de.lostmekka._3m5gamejam6.nextBoolean
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.internal.Zircon
import kotlin.math.abs
import kotlin.math.sign
import kotlin.random.Random

fun World.updateTorches() {
    gameArea.fetchBlocks().forEach { (_, block) ->
        block.currentEntities
            .filter { it.type is Torch }
            .forEach { it.tileAnimation.tick() }
    }
}

fun World.updateEnemies() {
    gameArea.fetchBlocks()
        .flatMap { (_, block) -> block.currentEntities.filter { it.type is Zombie } }
        .forEach { updateEnemy(it) }
}

fun World.updateEnemy(entity: AnyGameEntity) {
    val currentPos = entity.position
    val playerFound = findVisiblePositionsFor(currentPos, gameConfig.enemies.zombie.viewDistance)
        .any { player.position == it }
    if (playerFound) {
        if (!Random.nextBoolean(gameConfig.enemies.zombie.chaseChance)) return

        val dx = player.position2D.x - entity.position2D.x
        val dy = player.position2D.y - entity.position2D.y
        val adx = abs(dx)
        val ady = abs(dy)

        if (adx + ady <= 1) {
            // attack
            player.health -= gameConfig.enemies.zombie.damage
            if (player.health > 0) Zircon.eventBus.publish(SoundEvent(SoundEventType.PlayerHit))
        } else {
            // chase
            val moveX = when {
                adx > ady -> true
                adx < ady -> false
                else -> Random.nextBoolean()
            }
            val target = if (moveX) {
                currentPos.withRelativeX(dx.sign)
            } else {
                currentPos.withRelativeY(dy.sign)
            }
            moveEntity(entity, target)
        }
    } else {
        // no player >> Do some random movement
        if (!Random.nextBoolean(gameConfig.enemies.zombie.roamChance)) return

        val amount = Random.nextInt(2) * 2 - 1
        val target = if (Random.nextBoolean()) {
            currentPos.withRelativeX(amount)
        } else {
            currentPos.withRelativeY(amount)
        }
        moveEntity(entity, target)
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
                val baseProbability = gameConfig.madness.retreatChance
                val probability = baseProbability * (1.0 - 0.5 * block.averageSurroundingMadness)
                if (Random.nextBoolean(probability)) {
                    block.hasMadness = false
                }
            }
            !block.hasMadness -> {
                val baseProbability = gameConfig.madness.growthChance
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
    this[player.position]?.isLit = true
    if (!player.inventory.holdsSword && player.inventory.torches > 0) {
        floodLight(player.position, gameConfig.light.torchLightRadius)
    }

    torches.forEach { floodLight(it.position, gameConfig.light.torchLightRadius) }
    altars.forEach { floodLight(it.position, gameConfig.light.altarLightRadius) }
}

private fun World.floodLight(pos: Position3D, radius: Int) {
    if (this[pos]?.hasMadness != false) return
    findVisiblePositionsFor(pos, radius).forEach { this[it]?.isLit = true }
}

fun World.updateMadnessSoundVolume() {
    var madnessSum = 0
    for (x in player.position.x - 5..player.position.x + 5) {
        for (y in player.position.y - 5..player.position.y + 5) {
            if (this[Position3D.create(x, y, 0)]?.hasMadness == true) madnessSum += 1
        }
    }
    Zircon.eventBus.publish(MadnessExpanse(madnessSum))
}

