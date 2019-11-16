package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.config.gameConfig
import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import de.lostmekka._3m5gamejam6.entity.Enemy
import de.lostmekka._3m5gamejam6.entity.Summoner
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.Zombie
import de.lostmekka._3m5gamejam6.entity.attribute.LightEmitter
import de.lostmekka._3m5gamejam6.entity.attribute.findAttributeOrNull
import de.lostmekka._3m5gamejam6.entity.attribute.hasAttribute
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.entity.attribute.lightEmitter
import de.lostmekka._3m5gamejam6.entity.attribute.madnessStorage
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.entity.attribute.position2D
import de.lostmekka._3m5gamejam6.entity.attribute.storedPath
import de.lostmekka._3m5gamejam6.entity.attribute.tileAnimation
import de.lostmekka._3m5gamejam6.hammingDistanceTo
import de.lostmekka._3m5gamejam6.neighbours
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
        .flatMap { (_, block) -> block.currentEntities.filter { it.type is Enemy } }
        .forEach {
            when (it.type) {
                is Zombie -> updateZombie(it)
                is Summoner -> updateSummoner(it)
            }
        }
}

fun World.updateSummoner(entity: AnyGameEntity) {
    val fleeDistance = 20
    val madnessCollectThreshold = 5
    val madnessCollectAverageThreshold = 0.7
    val madnessSpreadThreshold = 25
    val madnessSpreadAverageThreshold = 0.1
    val madnessSpreadFarSeedChance = 0.05

    val position = entity.position
    val block = this[position] ?: return
    val madnessStorage = entity.madnessStorage
    val storedPath = entity.storedPath
    val path = storedPath.path

    if (madnessStorage.storedMadness <= madnessCollectThreshold) madnessStorage.collecting = true
    if (madnessStorage.storedMadness >= madnessSpreadThreshold) madnessStorage.collecting = false

    fun GameBlock.hasFreeMadness() =
        currentEntities.none { it.hasAttribute<LightEmitter>() }
                && hasMadness
                && averageMadnessMediumRadius >= madnessCollectAverageThreshold

    fun GameBlock.shouldReceiveMadness() =
        !hasMadness && averageMadnessMediumRadius <= madnessSpreadAverageThreshold

    fun putMadness() {
        (position.neighbours + position)
            .shuffled()
            .mapNotNull { this[it] }
            .forEach {
                if (!it.hasMadness && !madnessStorage.isEmpty()) {
                    madnessStorage.storedMadness--
                    it.hasMadness = true
                }
            }
    }

    fun takeMadness() {
        if (block.hasFreeMadness() && !madnessStorage.isFull()) {
            madnessStorage.storedMadness++
            block.hasMadness = false
        }
    }

    when {
        hasSightConnection(position, player.position, gameConfig.enemies.summoner.viewDistance) -> {
            // flee
            putMadness()
            val fleePath = findPath(position) { it hammingDistanceTo player.position > fleeDistance }
            storedPath.path = fleePath
            if (fleePath != null && fleePath.isNotEmpty()) moveEntity(entity, fleePath.first())
        }
        path != null && path.isNotEmpty() -> {
            if (madnessStorage.collecting) takeMadness()
            moveEntity(entity, path.removeAt(0))
        }
        madnessStorage.collecting -> {
            // collect madness
            if (block.hasFreeMadness()) {
                takeMadness()
            } else {
                storedPath.path = findPath(position) { this[it]?.hasFreeMadness() == true }
            }
        }
        else -> {
            // spread madness
            if (block.shouldReceiveMadness()) {
                putMadness()
            } else {
                storedPath.path = if (Random.nextBoolean(madnessSpreadFarSeedChance)) {
                    fetchRandomBlocks(3) { it.block.shouldReceiveMadness() }
                        .minBy { it.position hammingDistanceTo position }
                        ?.let { findPath(position, it.position) }
                } else {
                    findPath(position) { this[it]?.shouldReceiveMadness() == true }
                }
            }
        }
    }
}

fun World.updateZombie(entity: AnyGameEntity) {
    val currentPos = entity.position
    val playerFound = hasSightConnection(currentPos, player.position, gameConfig.enemies.zombie.viewDistance)
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

    calculateAverage(
        blocks = blocks,
        kernel = GaussKernels.small,
        valueGetter = { if (this[it]?.hasMadness == true) 1.0 else 0.0 },
        averageValueSetter = { pos, average -> this[pos]?.averageMadnessSmallRadius = average }
    )
    calculateAverage(
        blocks = blocks,
        kernel = GaussKernels.medium,
        valueGetter = { if (this[it]?.hasMadness == true) 1.0 else 0.0 },
        averageValueSetter = { pos, average -> this[pos]?.averageMadnessMediumRadius = average }
    )
    calculateAverage(
        blocks = blocks,
        kernel = GaussKernels.big,
        valueGetter = { if (this[it]?.hasMadness == true) 1.0 else 0.0 },
        averageValueSetter = { pos, average -> this[pos]?.averageMadnessBigRadius = average }
    )

    for ((pos, block) in blocks) {
        when {
            block.hasMadness && block.isLit -> {
                val baseProbability = gameConfig.madness.retreatChance
                val probability = baseProbability * (1.0 - 0.5 * block.averageMadnessSmallRadius)
                if (Random.nextBoolean(probability)) {
                    block.hasMadness = false
                }
            }
            !block.hasMadness -> {
                val baseProbability = gameConfig.madness.growthChance
                val probability = baseProbability * (1.0 - 0.5 * block.averageMadnessSmallRadius)
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
    player.lightEmitter.isEnabled = !player.inventory.holdsSword && player.inventory.torches > 0

    val emitters = mutableListOf<AnyGameEntity>()
    for ((_, block) in gameArea.fetchBlocks()) {
        block.isLit = false
        emitters += block.currentEntities.filter { it.findAttributeOrNull<LightEmitter>()?.isEnabled == true }
    }

    this[player.position]?.isLit = true
    emitters.forEach { floodLight(it.position, it.lightEmitter.radius) }
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

