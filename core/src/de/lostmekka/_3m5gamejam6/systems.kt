package de.lostmekka._3m5gamejam6

import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.KeyboardEvent

object Movable : BaseFacet<GameContext>() {

    override fun executeCommand(command: GameCommand<out EntityType>) =
        command.responseWhenCommandIs(MoveTo::class) { (context, entity, position) ->
            val world = context.world
            var result: Response = Pass
            if (world.moveEntity(entity, position)) {
                result = Consumed
            }

            result
        }
}

object InputReceiver : BaseBehavior<GameContext>() {

    override fun update(entity: GameEntity<out EntityType>, context: GameContext): Boolean {
        val (_, _, uiEvent, player) = context
        val currentPos = player.position
        if (uiEvent is KeyboardEvent) {
            val newPosition = when (uiEvent.code) {
                KeyCode.KEY_W -> currentPos.withRelativeY(-1)
                KeyCode.KEY_A -> currentPos.withRelativeX(-1)
                KeyCode.KEY_S -> currentPos.withRelativeY(1)
                KeyCode.KEY_D -> currentPos.withRelativeX(1)
                else -> {
                    currentPos
                }
            }
            player.executeCommand(MoveTo(context, player, newPosition))
        }
        return true
    }
}