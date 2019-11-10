package de.lostmekka._3m5gamejam6.entity.facet

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.entity.*
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object Equipable : BaseFacet<GameContext>() {
    override fun executeCommand(command: GameCommand<out EntityType>): Response {
        var response: Response = Pass

        command.whenCommandIs(EquipTorch::class) { (context, _) ->
            context.world.player.inventory.holdsSword = false
            response = Consumed
            true
        }

        command.whenCommandIs(EquipSword::class) { (context, _) ->
            context.world.player.inventory.holdsSword = true
            response = Consumed
            true
        }
        return response
    }
}