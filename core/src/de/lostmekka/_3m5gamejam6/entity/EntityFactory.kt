package de.lostmekka._3m5gamejam6.entity

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.GameTileRepository
import de.lostmekka._3m5gamejam6.entity.attribute.EntityHealth
import de.lostmekka._3m5gamejam6.entity.attribute.EntityInventory
import de.lostmekka._3m5gamejam6.entity.attribute.EntityPosition
import de.lostmekka._3m5gamejam6.entity.attribute.EntityTileAnimation
import de.lostmekka._3m5gamejam6.entity.behavior.InputReceiver
import de.lostmekka._3m5gamejam6.entity.facet.Equipable
import de.lostmekka._3m5gamejam6.entity.facet.TorchHandling
import de.lostmekka._3m5gamejam6.entity.facet.Movable
import org.hexworks.amethyst.api.Entities
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.random.Random

private fun <T : EntityType> newGameEntityOfType(
    type: T,
    init: EntityBuilder<T, GameContext>.() -> Unit
) = Entities.newEntityOfType(type, init)

object EntityFactory {
    fun newPlayer() = newGameEntityOfType(Player) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                tile = GameTileRepository.player,
                madnessTile = GameTileRepository.playerMadness
            ),
            EntityHealth(),
            EntityInventory()
        )
        behaviors(InputReceiver)
        facets(Movable, TorchHandling, Equipable)
    }

    fun newEnemyZombie() = newGameEntityOfType(EnemyZombie) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                tile = GameTileRepository.zombie,
                madnessTile = GameTileRepository.zombieMadness
            ),
            EntityHealth()
        )
        facets(Movable)
    }


    fun newTorchItem() = newGameEntityOfType(TorchItem) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(GameTileRepository.torchItem)
        )
    }

    fun newTorch(frame: Int = Random.nextInt()) = newGameEntityOfType(Torch) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                frames = GameTileRepository.torch,
                currentIndex = frame,
                madnessFrames = listOf(GameTileRepository.torchMadness)
            )
        )
    }
}
