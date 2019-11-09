package de.lostmekka._3m5gamejam6.config

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.data.impl.Size3D

object GameConfig {
    val logareaHeight = 20
    val sidebarWidth = 25
    val windowWidth = 120
    val windowHeight = 62
    val worldSize = Size3D.create(
        windowWidth - sidebarWidth,
        windowHeight, 1)
    val tileSet = CP437TilesetResources.rogueYun16x16()
    val theme = ColorThemes.zenburnVanilla()

    val isDebug = false

    val madnessProbability = 0.05
    val torchProbability = 0.1
    val madnessHealthDecrease = 10

    val fogOfWarEnabled = true
    val playerLightRadius = 2
    val torchLightRadius = 5
}
