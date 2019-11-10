package de.lostmekka._3m5gamejam6.entity.behavior

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.MoveTo
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.entity.attribute.position2D
import de.lostmekka._3m5gamejam6.nextBoolean
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.impl.Position3D
import kotlin.random.Random

object EnemyAI : BaseBehavior<GameContext>() {
    override fun update(entity: GameEntity<out EntityType>, context: GameContext): Boolean {
        val (world, _, uiEvent, player) = context
        val currentPos = entity.position
        var newPosition = Position3D.unknown()

        val playerfound = world
            .findVisiblePositionsFor(currentPos.to2DPosition(), GameConfig.enemyViewDistance)
            .any { player.position2D == it }
        if (playerfound) {
            println("Iam gonna kill you")

            var deltaX = player.position2D.x - entity.position2D.x
            var deltaY = player.position2D.y - entity.position2D.y

            newPosition = currentPos



            println("DeltaX " + deltaX)
            println("DeltaY " + deltaY)

            if (deltaX != 0) if (deltaX > 0) {
                newPosition =
                    newPosition.withRelativeX(+1)
                println("Chasing player x+1")
            } else {
                println("Chasing player x-1")
                newPosition = newPosition.withRelativeX(-1)
            }

            if (deltaY != 0) if (deltaY > 0) {
                println("Chasing player y+1")
                newPosition =
                    newPosition.withRelativeY(+1)
            } else {
                println("Chasing player y-1")
                newPosition = newPosition.withRelativeY(-1)
            }


        } else {
            newPosition = currentPos.withRelativeX(Random.nextInt(-1, 2))
            newPosition = newPosition.withRelativeY(Random.nextInt(-1, 2))
        }

        entity.executeCommand(MoveTo(context, entity, newPosition))



        if (player.position2D == entity.position2D) {
            //Deal some damage
            println("I got you")
            player.health -= GameConfig.enemyDamage
        }

        return true
    }
}
