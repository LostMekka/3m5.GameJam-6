package de.lostmekka._3m5gamejam6.config

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.data.impl.Size3D
import java.beans.Transient

val gameConfig = GameConfig()

data class GameConfig(
    val game: Game = Game(),
    val player: Player = Player(),
    val levelGeneration: LevelGeneration = LevelGeneration(),
    val window: Window = Window(),
    val light: Light = Light(),
    val enemies: Enemies = Enemies(),
    val madness: Madness = Madness(),
    val sound: Sound = Sound(),
    val debug: Debug = Debug()
) {
    data class Game(
        val levelCount: Int = 5
    )

    data class Player(
        val hp: Int = 100,
        val torchBuildingTime: Int = 4,
        val torchBuildingCost: Int = 1,
        val altarBuildingTime: Int = 6,
        val altarBuildingCost: Int = 3,
        val altarHealthBonus: Int = 10,
        val initialTorchCount: Int = 10,
        val damageMin: Int = 30,
        val damageMax: Int = 40
    )

    data class Enemies(
        val zombie: Zombie = Zombie(),
        val summoner: Summoner = Summoner()
    ) {
        data class Zombie(
            val hp: Int = 100,
            val damage: Int = 4,
            val viewDistance: Int = 6,
            val roamChance: Double = 0.3,
            val chaseChance: Double = 0.75
        )

        data class Summoner(
            val hp: Int = 100,
            val viewDistance: Int = 4,
            val fleeDistance: Int = 20,
            val madnessCollectThreshold: Int = 5,
            val madnessCollectAverageThreshold: Double = 0.7,
            val madnessSpreadThreshold: Int = 25,
            val madnessSpreadAverageThreshold: Double = 0.1,
            val madnessSpreadFarSeedChance: Double = 0.05
        )
    }

    data class Madness(
        val growthChance: Double = 0.01,
        val retreatChance: Double = 0.25,
        val damage: Int = 10
    )

    data class LevelGeneration(
        val alternateFloorChance: Double = 0.15,
        val alternateWallChance: Double = 0.20,
        val torchCount: Int = 60,
        val altarCount: Int = 5,
        val zombieCount: Int = 8,
        val zombieCountPerLevel: Int = 1,
        val summonerCount: Int = 0,
        val summonerCountPerLevel: Int = 2
    )

    data class Window(
        val width: Int = 90,
        val height: Int = 50,
        val sidebarWidth: Int = 25,
        val logAreaHeight: Int = 20
    )

    data class Light(
        val torchLightRadius: Int = 6,
        val altarLightRadius: Int = 8,
        val portalLightRadius: Int = 2
    )

    data class Sound(
        val whisperVolumeMin: Float = 0.1f,
        val whisperVolumeMax: Float = 1f,
        val backgroundMusicVolume: Float = 0.5f
    )

    data class Debug(
        val enableLogArea: Boolean = false,
        val enableTorchPlacement: Boolean = false,
        val seeEverything: Boolean = false
    )

    val worldSize
        @Transient
        get() = Size3D.create(window.width - window.sidebarWidth, window.height, 1)

    val tileSet
        @Transient
        get() = CP437TilesetResources.rogueYun16x16()

    val theme
        @Transient
        get() = ColorThemes.zenburnVanilla()
}
