package de.lostmekka._3m5gamejam6.entity.behavior

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.MoveTo
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.entity.attribute.position2D
import de.lostmekka._3m5gamejam6.nextBoolean
import de.lostmekka._3m5gamejam6.world.SoundEvent
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.internal.Zircon
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

object EnemyAI : BaseBehavior<GameContext>() {

    var deltaX: Int = 0
    var deltaY: Int = 0

    fun adaptY(currentPos: Position3D): Position3D {
        var newPosition = currentPos

        if (deltaY != 0) if (deltaY > 0) {
            newPosition =
                newPosition.withRelativeY(+1)
        } else {
            newPosition = newPosition.withRelativeY(-1)
        }

        return newPosition

    }

    fun adaptX(currentPos: Position3D): Position3D {
        var newPosition = currentPos

        if (deltaX != 0) if (deltaX > 0) {
            newPosition =
                newPosition.withRelativeX(+1)
        } else {
            newPosition = newPosition.withRelativeX(-1)
        }
        return newPosition

    }

    fun randomX(currentPos: Position3D): Position3D {
        return currentPos.withRelativeX(Random.nextInt(-1, 2))
    }

    fun randomY(currentPos: Position3D): Position3D {
        return currentPos.withRelativeY(Random.nextInt(-1, 2))
    }

    fun getDistance(pos1: Position3D, pos2: Position3D): Double {
        val dx: Double = (pos1.x - pos2.x).toDouble().pow(2.0)
        val dy: Double = (pos1.y - pos2.y).toDouble().pow(2.0)
        return sqrt(dx + dy)
    }

    override fun update(entity: GameEntity<out EntityType>, context: GameContext): Boolean {
        val (world, _, _, player) = context
        val currentPos = entity.position

        //check for isPlayer near???
        val playerFound = world
            .findVisiblePositionsFor(currentPos.to2DPosition(), GameConfig.enemyViewDistance)
            .any { player.position2D == it }
        if (playerFound) {
            if (Random.nextBoolean((GameConfig.enemyChasesPlayer))) {
                //if yes calc delta distance
                deltaX = player.position2D.x - entity.position2D.x
                deltaY = player.position2D.y - entity.position2D.y

                val pos1 = adaptX(currentPos)
                val pos2 = adaptY(currentPos)
                if (getDistance(pos1, player.position) >= getDistance(pos2, player.position)) {
                    entity.executeCommand(MoveTo(context, entity, pos2))
                } else {
                    entity.executeCommand(MoveTo(context, entity, pos1))
                }
            }
//no player >> Do some random movement
        } else {

            if (Random.nextBoolean((1 - GameConfig.enemySleeps))) {
                if (Random.nextBoolean(0.5)) {
                    entity.executeCommand(MoveTo(context, entity, randomX(currentPos)))
                } else {
                    entity.executeCommand(MoveTo(context, entity, randomY(currentPos)))
                }
            }
        }

        //check if player is caught >> kill player
        if (player.position2D == entity.position2D) {
            Zircon.eventBus.publish(SoundEvent("Hit"))
            player.health -= GameConfig.enemyDamage
            world.checkPlayerDeath()
        }
        return true
    }
}
