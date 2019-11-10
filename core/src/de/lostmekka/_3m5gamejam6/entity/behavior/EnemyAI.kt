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

        //check for isPlayer near???
        val playerfound = world
            .findVisiblePositionsFor(currentPos.to2DPosition(), GameConfig.enemyViewDistance)
            .any { player.position2D == it }
        if (playerfound) {

            //if yes calc delta distance
            var deltaX = player.position2D.x - entity.position2D.x
            var deltaY = player.position2D.y - entity.position2D.y

            newPosition = currentPos

            //decrese distance bitween player and enemy
            if (deltaX != 0) if (deltaX > 0) {
                newPosition =
                    newPosition.withRelativeX(+1)
            } else {
                newPosition = newPosition.withRelativeX(-1)
            }

            if (deltaY != 0) if (deltaY > 0) {
                newPosition =
                    newPosition.withRelativeY(+1)
            } else {
                newPosition = newPosition.withRelativeY(-1)
            }

//no player >> Do some random movement
        } else {
            newPosition = currentPos.withRelativeX(Random.nextInt(-1, 2))
            newPosition = newPosition.withRelativeY(Random.nextInt(-1, 2))
        }

        //finaly do the movement
        entity.executeCommand(MoveTo(context, entity, newPosition))


        //check if player is caught >> kill player
        if (player.position2D == entity.position2D) {
            player.health -= GameConfig.enemyDamage
            world.checkPlayerDeath()
        }

        return true
    }
}
