package de.lostmekka._3m5gamejam6.entity

import de.lostmekka._3m5gamejam6.GameContext
import de.lostmekka._3m5gamejam6.GameTileRepository
import de.lostmekka._3m5gamejam6.config.gameConfig
import de.lostmekka._3m5gamejam6.entity.attribute.EntityHealth
import de.lostmekka._3m5gamejam6.entity.attribute.EntityInventory
import de.lostmekka._3m5gamejam6.entity.attribute.EntityPosition
import de.lostmekka._3m5gamejam6.entity.attribute.EntityTileAnimation
import de.lostmekka._3m5gamejam6.entity.attribute.LightEmitter
import de.lostmekka._3m5gamejam6.entity.attribute.MadnessStorage
import de.lostmekka._3m5gamejam6.entity.attribute.StoredPath
import de.lostmekka._3m5gamejam6.entity.behavior.InputReceiver
import de.lostmekka._3m5gamejam6.entity.facet.Equipable
import de.lostmekka._3m5gamejam6.entity.facet.Movable
import de.lostmekka._3m5gamejam6.entity.facet.TorchHandling
import org.hexworks.amethyst.api.Entities
import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.random.Random

private fun <T : EntityType> newGameEntityOfType(
    type: T,
    init: EntityBuilder<T, GameContext>.() -> Unit
) = Entities.newEntityOfType(type, init)

object Player : BaseEntityType("Player") {
    fun create() = newGameEntityOfType(Player) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                tile = GameTileRepository.player,
                madnessTile = GameTileRepository.playerMadness
            ),
            EntityHealth(),
            EntityInventory(),
            LightEmitter(gameConfig.light.torchLightRadius)
        )
        behaviors(InputReceiver)
        facets(Movable, TorchHandling, Equipable)
    }
}

abstract class Enemy(name: String) : BaseEntityType(name)

object Zombie : Enemy("Zombie") {
    fun create() = newGameEntityOfType(Zombie) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                tile = GameTileRepository.zombie,
                madnessTile = GameTileRepository.zombieMadness
            ),
            EntityHealth(gameConfig.enemies.zombie.hp)
        )
        facets(Movable)
    }
}

object Summoner : Enemy("Summoner") {
    fun create() = newGameEntityOfType(Summoner) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                tile = GameTileRepository.summoner,
                madnessTile = GameTileRepository.summonerMadness
            ),
            EntityHealth(),
            MadnessStorage(30),
            StoredPath()
        )
        facets(Movable)
    }
}

object TorchItem : BaseEntityType("Torch Item") {
    fun create() = newGameEntityOfType(TorchItem) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(GameTileRepository.torchItem)
        )
    }
}

object Torch : BaseEntityType("Torch") {
    fun create(frame: Int = Random.nextInt()) = newGameEntityOfType(Torch) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(
                frames = GameTileRepository.torch,
                currentIndex = frame,
                madnessFrames = listOf(GameTileRepository.torchMadness)
            ),
            LightEmitter(gameConfig.light.torchLightRadius)
        )
    }
}

object ActivatedAltar : BaseEntityType("Activated Altar") {
    fun create() = newGameEntityOfType(ActivatedAltar) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(GameTileRepository.altarActivated),
            LightEmitter(gameConfig.light.altarLightRadius)
        )
    }
}

object OpenedPortal : BaseEntityType("Opened Portal") {
    fun create() = newGameEntityOfType(OpenedPortal) {
        attributes(
            EntityPosition(),
            EntityTileAnimation(GameTileRepository.portalActivated),
            LightEmitter(gameConfig.light.portalLightRadius)
        )
    }
}
