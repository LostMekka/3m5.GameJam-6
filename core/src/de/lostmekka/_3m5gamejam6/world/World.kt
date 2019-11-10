package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.EnemyZombie
import de.lostmekka._3m5gamejam6.entity.EntityFactory
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.to3DPosition
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.Engines
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.data.impl.Size3D
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.shape.EllipseFactory
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.uievent.UIEvent
import org.hexworks.zircon.internal.Zircon
import kotlin.random.Random

class World(
    visibleSize: Size3D,
    actualSize: Size3D,
    private var levelDepth: Int = 0
) {
    val gameArea = GameAreaBuilder.newBuilder<Tile, GameBlock>()
        .withVisibleSize(visibleSize)
        .withActualSize(actualSize)
        .withDefaultBlock(GameBlock.floor1())
        .withLayersPerBlock(1)
        .build()

    val player = EntityFactory.newPlayer()

    val engine: Engine<GameContext> = Engines.newEngine()

    var altarCount = 0
    var activatedAltarCount = 0
    lateinit var stairsPosition: Position3D

    operator fun get(pos: Position) = this[pos.to3DPosition()]

    operator fun get(pos: Position3D) = gameArea
        .fetchBlockAt(pos)
        .let { if (it.isPresent) it.get() else null }

    operator fun set(pos: Position, block: GameBlock) {
        this[pos.to3DPosition()] = block
    }

    operator fun set(pos: Position3D, block: GameBlock) {
        gameArea.setBlockAt(pos, block)
    }

    fun hasMadnessAt(pos: Position) = this[pos]?.hasMadness ?: false
    fun hasMadnessAt(pos: Position3D) = this[pos]?.hasMadness ?: false

    private fun bothBlocksPresentAndWalkable(oldBlock: Maybe<GameBlock>, newBlock: Maybe<GameBlock>) =
        oldBlock.isPresent && newBlock.isPresent && newBlock.get().isWalkable


    fun movePlayer(position: Position3D): Boolean {
        val success = moveEntity(player, position)
        if (success) onPlayerMoved()
        return success
    }

    private fun checkEnemyDamage(position: Position3D) {
        if (player.inventory.holdsSword) {
            get(position)?.currentEntities?.filter { it.type == EnemyZombie }?.forEach {
                it.health -= Random.nextInt(GameConfig.SwordDamageMin, GameConfig.SwordDamageMax)
                if (it.health <= 0) engine.removeEntity(it)
            }
            get(position)?.currentEntities?.removeIf {it.type == EnemyZombie && it.health <= 0 }
        }
    }

    fun moveEntity(entity: GameEntity<EntityType>, position: Position3D): Boolean {
        var success = false
        val oldBlock = gameArea.fetchBlockAt(entity.position)
        val newBlock = gameArea.fetchBlockAt(position)

        checkEnemyDamage(player.position)

        if (bothBlocksPresentAndWalkable(oldBlock, newBlock)) {
            // walk
            success = true
            oldBlock.get().currentEntities -= entity
            entity.position = position
            newBlock.get().currentEntities += entity
        }

        return success
    }

    fun checkPlayerDeath() {
        if (player.health <= 0) {
            Zircon.eventBus.publish(PlayerDied("You died because of madness!"))
        }
    }

    private fun checkPlayerMadness(block: GameBlock) {
        var madnessSum = 0
        if (block.hasMadness) player.health -= GameConfig.madnessHealthDecrease
        for (x in player.position.x - 5 .. player.position.x + 5) {
            for (y in player.position.y - 5 .. player.position.y + 5) {
                if (this[Position3D.create(x, y, 0)]?.hasMadness == true) madnessSum += 1
            }
        }
        Zircon.eventBus.publish(MadnessExpanse(madnessSum))
    }

    fun onKeyInput(screen: Screen, uiEvent: UIEvent) {
        engine.update(
            GameContext(
                world = this,
                screen = screen,
                uiEvent = uiEvent,
                player = player
            )
        )
    }

    fun onPlayerMoved() {
        player.inventory.buildingProgress = 0
        if (this[player.position]?.isDoor == true) {
            Zircon.eventBus.publish(SoundEvent("Door"))
        } else if (this[player.position]?.isStairs == true) {
            // TODO: check all altars
            // go to next level
            if (levelDepth + 1 < GameConfig.levelCount) {
                Zircon.eventBus.publish(SoundEvent("NextLevel"))
                Zircon.eventBus.publish(NextLevel(levelDepth + 1))
            } else {
                Zircon.eventBus.publish(SoundEvent("YouWon"))
                Zircon.eventBus.publish(WON)
            }
        } else {
            Zircon.eventBus.publish(SoundEvent("Step"))
        }

    }

    fun tick() {
        updateTorches()
        updateLighting()
        updateMadness()
        updateEnemyZombies()
        checkPlayerMadness(gameArea.fetchBlockAt(player.position).get())
        checkPlayerDeath()
    }

    fun findVisiblePositionsFor(pos: Position, radius: Int): Iterable<Position> {
        val boundary = EllipseFactory.buildEllipse(
            fromPosition = pos,
            toPosition = pos.withRelativeX(radius).withRelativeY(radius)
        )
        return boundary
            .positions()
            .flatMap { ringPos ->
                val result = mutableSetOf<Position>()
                val line1 = LineFactory.buildLine(pos, ringPos).toList()
                // line2 is necessary since there are direction dependent rounding errors that produce unwanted blind spots
                val line2 = line1.map { Position.create(it.x, 2 * pos.y - it.y) }
                val n1 = line1.takeWhile { it == pos || this[it]?.isTransparent == true }.size
                val n2 = line2.takeWhile { it == pos || this[it]?.isTransparent == true }.size
                result += line1.take(n1 + 1)
                result += line2.take(n2 + 1)
                result
            }
    }

    fun neighboursOf(position: Position3D): List<GameBlock> {
        val ans = mutableListOf<GameBlock>()
        for (x in position.x - 1..position.x + 1) {
            for (y in position.y - 1..position.y + 1) {
                this[Position3D.create(x, y, 0)]?.also { ans += it }
            }
        }
        return ans
    }

    fun fetchSpawnableBlocks() = gameArea.fetchBlocks()
        .filter { !it.block.isAltar && it.block.isWalkable && it.block.isTransparent && it.block.currentEntities.isEmpty() }
        .filter {
            val neighbours = neighboursOf(it.position)
            neighbours.size == 9 && neighbours.all { it.isTransparent && it.isWalkable }
        }

    fun fetchRandomSpawnableBlocks(count: Int) = fetchSpawnableBlocks()
        .shuffled()
        .take(count)

}
