package de.lostmekka._3m5gamejam6.entity.behavior

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.entity.*
import de.lostmekka._3m5gamejam6.entity.attribute.position
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.KeyboardEvent

object InputReceiver : BaseBehavior<GameContext>() {
    override fun update(entity: GameEntity<out EntityType>, context: GameContext): Boolean {
        val (_, _, uiEvent, player) = context
        val currentPos = player.position

        if (uiEvent is KeyboardEvent) {
            when {
                // grab torch item
                uiEvent.code == KeyCode.KEY_G -> player.executeCommand(GrabTorchItem(context, player, currentPos))

                // drop torch
                uiEvent.code == KeyCode.KEY_T -> player.executeCommand(BuildTorch(context, player, currentPos))

                // equip torch
                uiEvent.code == KeyCode.DIGIT_1 -> player.executeCommand(EquipTorch(context, player))

                // equip sword
                uiEvent.code == KeyCode.DIGIT_2 -> player.executeCommand(EquipSword(context, player))

                // move
                else -> {
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
            }
        }

        return true
    }
}
