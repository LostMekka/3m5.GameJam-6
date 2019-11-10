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

    const val enableDebugLogArea = false
    const val enableDebugTorchPlacement = false

    const val madnessGrowthProbability = 0.02
    const val madnessRetreatProbability = 0.6
    const val madnessHealthDecrease = 10

    const val fogOfWarEnabled = false
    const val torchLightRadius = 6
    const val altarLightRadius = 8

    const val torchBuildingTime = 5
    const val torchBuildingCost = 1
    const val altarBuildingTime = 6
    const val altarBuildingCost = 3

    const val playerInitialTorchCount = 10
    const val areaInitialTorchCount = 75
    const val areaAltarCount = 1
}
