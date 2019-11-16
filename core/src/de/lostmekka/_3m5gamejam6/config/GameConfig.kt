package de.lostmekka._3m5gamejam6.config

import com.sksamuel.hoplite.ConfigFilePropertySource
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.FileSource
import com.sksamuel.hoplite.parsers.defaultParserRegistry
import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.data.impl.Size3D
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.beans.Transient
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

private const val configFileName = "config.yaml"
private const val defaultsFileName = "config.defaults.yaml"
private val defaultsFileComment = """
    # To configure the game:
    # Create a file named $configFileName, copy the config fields you want to change into it and change them there.
    # This file will be overwritten with the default values every time the game is started.
""".trimIndent()

val gameConfig = loadConfig()

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
) : PropertyHolder {
    data class Game(
        val levelCount: Int = 5
    ) : PropertyHolder

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
    ) : PropertyHolder

    data class Enemies(
        val zombie: Zombie = Zombie(),
        val summoner: Summoner = Summoner()
    ) : PropertyHolder {
        data class Zombie(
            val hp: Int = 100,
            val damage: Int = 4,
            val viewDistance: Int = 6,
            val roamChance: Double = 0.3,
            val chaseChance: Double = 0.75
        ) : PropertyHolder

        data class Summoner(
            val hp: Int = 100,
            val viewDistance: Int = 4
            // TODO: add other constants
        ) : PropertyHolder
    }

    data class Madness(
        val growthChance: Double = 0.01,
        val retreatChance: Double = 0.25,
        val damage: Int = 10
    ) : PropertyHolder

    data class LevelGeneration(
        val alternateFloorChance: Double = 0.15,
        val alternateWallChance: Double = 0.20,
        val torchCount: Int = 60,
        val altarCount: Int = 5,
        val zombieCount: Int = 8,
        val zombieCountPerLevel: Int = 1,
        val summonerCount: Int = 0,
        val summonerCountPerLevel: Int = 2
    ) : PropertyHolder

    data class Window(
        val width: Int = 90,
        val height: Int = 50,
        val sidebarWidth: Int = 25,
        val logAreaHeight: Int = 20
    ) : PropertyHolder

    data class Light(
        val torchLightRadius: Int = 6,
        val altarLightRadius: Int = 8,
        val portalLightRadius: Int = 2
    ) : PropertyHolder

    data class Sound(
        val whisperVolumeMin: Float = 0.1f,
        val whisperVolumeMax: Float = 1f,
        val backgroundMusicVolume: Float = 0.5f
    ) : PropertyHolder

    data class Debug(
        val enableLogArea: Boolean = false,
        val enableTorchPlacement: Boolean = false,
        val seeEverything: Boolean = false
    ) : PropertyHolder

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

private fun loadConfig(): GameConfig {
    val configFile = File(configFileName)
    val defaultsFile = File(defaultsFileName)
    val configWasMissing = !configFile.exists()

    if (configWasMissing) {
        // this initial dummy config file is actually needed.
        // hoplite can fill the config objects with default values,
        // but it will crash if the file does not exist or is empty...
        configFile.writeText("hopliteIsDrunk: true")
    }
    val config = try {
        ConfigLoader()
            .withPropertySource(
                ConfigFilePropertySource(
                    file = FileSource.PathSource(Paths.get(configFileName)),
                    parserRegistry = defaultParserRegistry()
                )
            )
            .loadConfigOrThrow<GameConfig>()
    } finally {
        if (configWasMissing) configFile.delete()
    }

    val dumperOptions = DumperOptions().apply {
        isPrettyFlow = true
        defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
    }
    val defaultValues = Yaml(dumperOptions).dump(GameConfig().toMap())
    defaultsFile.writeText("$defaultsFileComment\n$defaultValues")

    return config
}

private interface PropertyHolder {
    // :scream: :facepalm: :scream:
    // yes, reflection. because hoplite needs data classes, but snakeyaml cannot handle data classes.
    @Suppress("UNCHECKED_CAST")
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        val kClass: KClass<*> = this::class
        for (property in kClass.declaredMemberProperties) {
            if (property.getter.annotations.any { it is Transient }) continue
            val value = (property as KProperty1<Any, Any>).get(this)
            map[property.name] = if (value is PropertyHolder) value.toMap() else value
        }
        return map
    }
}
