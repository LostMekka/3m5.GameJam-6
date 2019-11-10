package de.lostmekka._3m5gamejam6.entity.facet

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.ActivateAltar
import de.lostmekka._3m5gamejam6.entity.ActivatedAltar
import de.lostmekka._3m5gamejam6.entity.BuildTorch
import de.lostmekka._3m5gamejam6.entity.GameCommand
import de.lostmekka._3m5gamejam6.entity.GrabTorchItem
import de.lostmekka._3m5gamejam6.entity.Player
import de.lostmekka._3m5gamejam6.entity.Torch
import de.lostmekka._3m5gamejam6.entity.TorchItem
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.world.activateAltar
import de.lostmekka._3m5gamejam6.world.placeTorch
import de.lostmekka._3m5gamejam6.world.updateLighting
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.impl.Position3D

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
               val success = build(
                  context = context,
                  position = position,
                  buildingCost = GameConfig.torchBuildingCost,
                  buildingTime = GameConfig.torchBuildingTime,
                  buildingType = Torch,
                  buildOperation = { context.world.placeTorch(it) }
              )

              if (success) response = Consumed
              success

          }

        command.whenCommandIs(ActivateAltar::class) { (context, _, position) ->
            val success = build(
                context = context,
                position = position,
                buildingCost = GameConfig.altarBuildingCost,
                buildingTime = GameConfig.altarBuildingTime,
                buildingType = ActivatedAltar,
                buildOperation = { context.world.activateAltar(it) }
            )
            if (success) response = Consumed
            success
        }

        return response
    }

    private fun build(
        context: GameContext,
        position: Position3D,
        buildingCost: Int,
        buildingTime: Int,
        buildingType: EntityType,
        buildOperation: (Position3D) -> Boolean
    ): Boolean {
        val block = context.world[position] ?: return false
        if (block.currentEntities.any { it.type !is Player }) return false
        // check progress of build
        val inventory = context.world.player.inventory

        if (inventory.buildingType != buildingType) {
            inventory.buildingProgress = 0
            inventory.maxBuildingProgress = buildingTime
            inventory.buildingType = buildingType
        }

        val hasResources = inventory.torches >= buildingCost
        val buildingDone = inventory.buildingProgress >= buildingTime
        println("${inventory.buildingProgress} / $buildingTime")

        when {
            !hasResources -> {
                inventory.buildingProgress = 0
                inventory.maxBuildingProgress = 0
                inventory.buildingType = null
            }
            !buildingDone -> {
                inventory.buildingProgress += 1
            }
            buildOperation(position) -> {
                inventory.torches -= buildingCost
                inventory.buildingProgress = 0
                inventory.maxBuildingProgress = 0
                inventory.buildingType = null
            }
        }
        return true
    }
}
