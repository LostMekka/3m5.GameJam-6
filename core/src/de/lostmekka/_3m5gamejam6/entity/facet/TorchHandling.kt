package de.lostmekka._3m5gamejam6.entity.facet

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.entity.BuildTorch
import de.lostmekka._3m5gamejam6.entity.EntityFactory
import de.lostmekka._3m5gamejam6.entity.GameCommand
import de.lostmekka._3m5gamejam6.entity.GrabTorchItem
import de.lostmekka._3m5gamejam6.entity.TorchItem
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object TorchHandling : BaseFacet<GameContext>() {
    override fun executeCommand(command: GameCommand<out EntityType>): Response {
        var response: Response = Pass

        command.whenCommandIs(GrabTorchItem::class) { (context, _, position) ->
            val block = context.world.gameArea.fetchBlockAt(position)
            if (block.isPresent) {
                block.get().currentEntities.filter { it.type is TorchItem }.forEach {
                    context.world.gameArea.fetchBlockOrDefault(position).currentEntities -= it
                    context.world.engine.removeEntity(it)
                    //Increase Player Torches
                    context.world.player.inventory.torches += 1
                }
            }
            response = Consumed
            true
        }

        command.whenCommandIs(BuildTorch::class) { (context, _, position) ->
            val block = context.world.gameArea.fetchBlockAt(position)
            if (context.world.player.inventory.torches > 0) {
                context.world.player.inventory.torches -= 1
                if (block.isPresent) {
                    context.world.placeTorch(position)
                }
            }
            response = Consumed
            true
        }
        return response
    }
}
