package de.lostmekka._3m5gamejam6.config

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.data.impl.Size3D

object GameConfig {
    const val logAreaHeight = 20
    const val sidebarWidth = 25
    const val windowWidth = 120
    const val windowHeight = 62

    val worldSize = Size3D.create(windowWidth - sidebarWidth, windowHeight, 1)
    val tileSet = CP437TilesetResources.rogueYun16x16()
    val theme = ColorThemes.zenburnVanilla()

    const val isDebug = false

    const val madnessProbability = 0.05
    const val madnessHealthDecrease = 10

    const val fogOfWarEnabled = false
    const val playerLightRadius = 2
    const val torchLightRadius = 5
}