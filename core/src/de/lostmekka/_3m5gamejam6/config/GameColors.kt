package de.lostmekka._3m5gamejam6.config

import org.hexworks.zircon.api.TileColors

object GameColors {
    val shadowFg = TileColors.fromString("#111111")
    val shadowBg = TileColors.fromString("#000000")

    val wallFg = TileColors.fromString("#75715E")
    val wallFgMadness = TileColors.fromString("#4b2b5e")
    val wallBg = TileColors.fromString("#3E3D32")
    val wallBgMadness = TileColors.fromString("#000000")

    val floorFg = TileColors.fromString("#555144")
    val floorFgMadness = TileColors.fromString("#4b2b5e")
    val floorBg = TileColors.fromString("#1e2320")
    val floorBgMadness = TileColors.fromString("#000000")

    val doorFg = TileColors.fromString("#FFCD22")
    val doorFgMadness = TileColors.fromString("#8923c7")

    val altarFg = TileColors.fromString("#4d5645")
    val altarFgMadness = TileColors.fromString("#4d2675")
    val altarFgActivated = TileColors.fromString("#CdE645")

    val torchItemFg = TileColors.fromString("#FF834C")

    val zombieFg = TileColors.fromString("#E62020")
    val zombieBg = TileColors.fromString("#000000")

    val torchFgColors = listOf(
        TileColors.fromString("#FF834C"),
        TileColors.fromString("#FF954C"),
        TileColors.fromString("#FFC923")
    )

}
