package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.data.impl.Size3D

object GameConfig {
    val sidebarWidth = 25
    val windowWidth = 100
    val windowHeight = 60
    val worldSize = Size3D.create(windowWidth - sidebarWidth, windowHeight, 1)
    val tileSet = CP437TilesetResources.rogueYun16x16()
    val theme = ColorThemes.zenburnVanilla()
}
