package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.config.gameConfig
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.Player
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

class World(
    visibleSize: Size3D,
    actualSize: Size3D,
    val levelDepth: Int = 0
) {
    val gameArea = GameAreaBuilder.newBuilder<Tile, GameBlock>()
        .withVisibleSize(visibleSize)
        .withActualSize(actualSize)
        .withDefaultBlock(GameBlock.floor1())
        .withLayersPerBlock(1)
        .build()

    val player = Player.create()

    val engine: Engine<GameContext> = Engines.newEngine()

    var altarCount = 0
    var activatedAltarCount = 0
    lateinit var portalPosition: Position3D

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

    fun moveEntity(entity: GameEntity<EntityType>, position: Position3D): Boolean {
        var success = false
        val oldBlock = gameArea.fetchBlockAt(entity.position)
        val newBlock = gameArea.fetchBlockAt(position)

        if (bothBlocksPresentAndWalkable(oldBlock, newBlock)) {
            // walk
            success = true
            oldBlock.get().currentEntities -= entity
            entity.position = position
            newBlock.get().currentEntities += entity
        }

        return success
    }

    private fun checkPlayerDeath() {
        if (player.health <= 0) {
            Zircon.eventBus.publish(SoundEvent(SoundEventType.PlayerDeath))
            Zircon.eventBus.publish(PlayerDied("You died because of madness!"))
        }
    }

    private fun checkPlayerMadness() {
        val block = this[player.position] ?: return
        if (block.hasMadness) {
            player.health -= gameConfig.madness.damage
            if (player.health > 0) Zircon.eventBus.publish(SoundEvent(SoundEventType.PlayerHit))
            Zircon.eventBus.publish(SoundEvent(SoundEventType.MadnessHit))
        }
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

    private fun onPlayerMoved() {
        player.inventory.buildingProgress = 0
        if (this[player.position]?.isDoor == true) {
            Zircon.eventBus.publish(SoundEvent(SoundEventType.Door))
        } else if (this[player.position]?.portalIsOpen == true) {
            // go to next level
            if (levelDepth + 1 < gameConfig.game.levelCount) {
                Zircon.eventBus.publish(SoundEvent(SoundEventType.NextLevel))
                Zircon.eventBus.publish(NextLevel(levelDepth + 1))
            } else {
                Zircon.eventBus.publish(WON)
            }
        } else {
            Zircon.eventBus.publish(SoundEvent(SoundEventType.Step))
        }
    }

    fun tick() {
        updateTorches()
        updateMadness()
        updateMadnessSoundVolume()
        updateEnemyZombies()
        updateLighting()
        checkPlayerMadness()
        checkPlayerDeath()
    }

    fun findVisiblePositionsFor(center: Position3D, radius: Int): Iterable<Position3D> {
        val center2D = center.to2DPosition()
        return listOf(radius - 1, radius)
            .flatMap {
                EllipseFactory
                    .buildEllipse(center2D, center2D.withRelativeX(it).withRelativeY(it))
                    .positions()
            }
            .toSet()
            .flatMap { ringPos ->
                val result = mutableSetOf<Position3D>()
                val line = LineFactory.buildLine(center2D, ringPos).toList()

                var persistence = 1
                var obstructed = false
                for (linePos in line) {
                    val worldPos = linePos.toPosition3D(center.z)
                    if (obstructed) {
                        persistence--
                        if (persistence <= 0) break
                    } else {
                        val isFirst = linePos == center2D
                        val block = this[worldPos]
                        val transparent = block?.isTransparent ?: false
                        val walkable = block?.isWalkable ?: false
                        if (walkable && !transparent && !isFirst) persistence++
                        if (!transparent) obstructed = !isFirst
                    }
                    result += worldPos
                }
                result
            }
    }

    private fun neighboursOf(position: Position3D): List<GameBlock> {
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
