package de.lostmekka._3m5gamejam6.entity.facet

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.entity.BuildTorch
import de.lostmekka._3m5gamejam6.entity.GameCommand
import de.lostmekka._3m5gamejam6.entity.GrabTorchItem
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.TorchItem
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.world.placeTorch
import de.lostmekka._3m5gamejam6.world.updateLighting
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object TorchHandling : BaseFacet<GameContext>() {
    override fun executeCommand(command: GameCommand<out EntityType>): Response {
        var response: Response = Pass

        command.whenCommandIs(GrabTorchItem::class) { (context, _, position) ->
            context.world[position]?.let { block ->
                block.currentEntities
                    .filter { it.type is TorchItem || it.type is Torch }
                    .forEach {
                        block.currentEntities -= it
                        context.world.engine.removeEntity(it)
                        context.world.player.inventory.torches++
                    }
                context.world.updateLighting()
            }
            response = Consumed
            true
        }

        command.whenCommandIs(BuildTorch::class) { (context, _, position) ->
            if (context.world.player.inventory.torches > 0 && context.world.placeTorch(position)) {
                context.world.player.inventory.torches -= 1
            }
            response = Consumed
            true
        }
        return response
    }
}
