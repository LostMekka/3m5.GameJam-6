package de.lostmekka._3m5gamejam6.entity.behavior

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.entity.ActivateAltar
import de.lostmekka._3m5gamejam6.entity.BuildTorch
import de.lostmekka._3m5gamejam6.entity.EquipSword
import de.lostmekka._3m5gamejam6.entity.EquipTorch
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.GrabTorchItem
import de.lostmekka._3m5gamejam6.entity.MoveTo
import de.lostmekka._3m5gamejam6.entity.attribute.position
import de.lostmekka._3m5gamejam6.world.ValidInput
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.KeyboardEvent
import org.hexworks.zircon.internal.Zircon

object InputReceiver : BaseBehavior<GameContext>() {
    override fun update(entity: GameEntity<out EntityType>, context: GameContext): Boolean {
        val (_, _, uiEvent, player) = context
        val currentPos = player.position
        var commandResponse = if (uiEvent is KeyboardEvent) {
            when {
                // grab torch item
                uiEvent.code == KeyCode.KEY_G -> player.executeCommand(GrabTorchItem(context, player, currentPos))

                // build torch
                uiEvent.code == KeyCode.KEY_T -> player.executeCommand(BuildTorch(context, player, currentPos))

                // activate altar
                uiEvent.code == KeyCode.KEY_E -> player.executeCommand(ActivateAltar(context, player, currentPos))

                // equip torch
                uiEvent.code == KeyCode.DIGIT_1 -> player.executeCommand(EquipTorch(context, player))

                // equip sword
                uiEvent.code == KeyCode.DIGIT_2 -> player.executeCommand(EquipSword(context, player))

                // move
                uiEvent.code == KeyCode.KEY_W -> player.executeCommand(MoveTo(context, player, currentPos.withRelativeY(-1)))
                uiEvent.code == KeyCode.KEY_A -> player.executeCommand(MoveTo(context, player, currentPos.withRelativeX(-1)))
                uiEvent.code == KeyCode.KEY_S -> player.executeCommand(MoveTo(context, player, currentPos.withRelativeY(1)))
                uiEvent.code == KeyCode.KEY_D -> player.executeCommand(MoveTo(context, player, currentPos.withRelativeX(1)))

                // wait
                uiEvent.code == KeyCode.SPACE -> Consumed

                else -> Pass
            }
        } else {
            Pass
        }

        val wasValidInput = commandResponse is Consumed
        if (wasValidInput) Zircon.eventBus.publish(ValidInput)
        return wasValidInput
    }
}
