package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.MoveTo
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.entity.attribute.position2D
import de.lostmekka._3m5gamejam6.entity.behavior.EnemyAI
import de.lostmekka._3m5gamejam6.nextBoolean
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.internal.Zircon
import kotlin.math.sqrt
import kotlin.random.Random

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
    var newPosition = currentPos.withRelativeX(Random.nextInt(-1, 2))
    return newPosition

}

fun randomY(currentPos: Position3D): Position3D {
    var newPosition = currentPos.withRelativeY(Random.nextInt(-1, 2))
    return newPosition

}

fun getDistance(pos1: Position3D, pos2: Position3D): Double {

    var dx: Double = Math.pow((pos1.x - pos2.x).toDouble(),2.0)
    var dy: Double = Math.pow((pos1.y - pos2.y).toDouble(),2.0)

    var dist = sqrt(dx + dy)


    return dist
}



fun World.updateEnemyZombie(entity: GameEntity<EntityType>)
{


    val currentPos = entity.position
//check for isPlayer near???
    val playerfound = this
        .findVisiblePositionsFor(currentPos.to2DPosition(), GameConfig.enemyViewDistance)
        .any { player.position2D == it }
    if (playerfound) {

        if (Random.nextBoolean((GameConfig.enemyChasesPlayer))) {

            //if yes calc delta distance
            EnemyAI.deltaX = player.position2D.x - entity.position2D.x
            EnemyAI.deltaY = player.position2D.y - entity.position2D.y


            val pos1 = EnemyAI.adaptX(currentPos)
            val pos2 = EnemyAI.adaptY(currentPos)

            if (EnemyAI.getDistance(pos1, player.position) >= EnemyAI.getDistance(pos2, player.position)) {
                moveEntity(entity, pos2)
            } else {
                moveEntity( entity, pos1)
            }
        }
//no player >> Do some random movement
    } else {

        if (Random.nextBoolean((1 - GameConfig.enemySleeps))) {

            if (Random.nextBoolean(0.5)) {
                moveEntity( entity, EnemyAI.randomX(currentPos))
            } else {
                moveEntity( entity, EnemyAI.randomY(currentPos))
            }
        }
    }

    //check if player is caught >> kill player
    if (player.position2D == entity.position2D) {
        Zircon.eventBus.publish(SoundEvent("Hit"))
        player.health -= GameConfig.enemyDamage
        this.checkPlayerDeath()
    }


}