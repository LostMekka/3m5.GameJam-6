package de.lostmekka._3m5gamejam6

import de.lostmekka._3m5gamejam6.config.GameColors
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.CharacterTile

object GameTileRepository {

    val empty: CharacterTile = Tiles.empty()

    val shadow: CharacterTile = Tiles.newBuilder()
        .withCharacter(' ')
        .withForegroundColor(GameColors.shadowFg)
        .withBackgroundColor(GameColors.shadowBg)
        .buildCharacterTile()

    val floor1: CharacterTile = Tiles.newBuilder()
        .withCharacter('.')
        .withForegroundColor(GameColors.floor1Fg)
        .withBackgroundColor(GameColors.floor1Bg)
        .buildCharacterTile()

    val floor2: CharacterTile = Tiles.newBuilder()
        .withCharacter(',')
        .withForegroundColor(GameColors.floor2Fg)
        .withBackgroundColor(GameColors.floor2Bg)
        .buildCharacterTile()

    val floorMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('~')
        .withForegroundColor(GameColors.floorFgMadness)
        .withBackgroundColor(GameColors.floorBgMadness)
        .buildCharacterTile()

    val wall1: CharacterTile = Tiles.newBuilder()
        .withCharacter('#')
        .withForegroundColor(GameColors.wall1Fg)
        .withBackgroundColor(GameColors.wall1Bg)
        .buildCharacterTile()

    val wall2: CharacterTile = Tiles.newBuilder()
        .withCharacter('+')
        .withForegroundColor(GameColors.wall2Fg)
        .withBackgroundColor(GameColors.wall2Bg)
        .buildCharacterTile()

    val wallMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('#')
        .withForegroundColor(GameColors.wallFgMadness)
        .withBackgroundColor(GameColors.wallBgMadness)
        .buildCharacterTile()

    val door: CharacterTile = Tiles.newBuilder()
        .withCharacter('H')
        .withForegroundColor(GameColors.doorFg)
        .withBackgroundColor(GameColors.wall1Bg)
        .buildCharacterTile()

    val doorMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('H')
        .withForegroundColor(GameColors.doorFgMadness)
        .withBackgroundColor(GameColors.wallBgMadness)
        .buildCharacterTile()

    val altar: CharacterTile = Tiles.newBuilder()
        .withCharacter('a')
        .withForegroundColor(GameColors.altarFg)
        .withBackgroundColor(GameColors.floor1Bg)
        .buildCharacterTile()

    val altarMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('a')
        .withForegroundColor(GameColors.altarFgMadness)
        .withBackgroundColor(GameColors.floorBgMadness)
        .buildCharacterTile()

    val altarActivated: CharacterTile = Tiles.newBuilder()
        .withCharacter('A')
        .withForegroundColor(GameColors.altarFgActivated)
        .withBackgroundColor(GameColors.floor1Bg)
        .buildCharacterTile()

    val portal: CharacterTile = Tiles.newBuilder()
        .withCharacter('p')
        .withForegroundColor(GameColors.altarFg)
        .withBackgroundColor(GameColors.floor1Bg)
        .buildCharacterTile()

    val portalMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('p')
        .withForegroundColor(GameColors.altarFgMadness)
        .withBackgroundColor(GameColors.floorBgMadness)
        .buildCharacterTile()

    val portalActivated: CharacterTile = Tiles.newBuilder()
        .withCharacter('P')
        .withForegroundColor(GameColors.altarFgActivated)
        .withBackgroundColor(GameColors.floor1Bg)
        .buildCharacterTile()

    val player = Tiles.newBuilder()
        .withCharacter('@')
        .withForegroundColor(GameColors.doorFg)
        .withBackgroundColor(GameColors.floor1Bg)
        .buildCharacterTile()

    val playerMadness = Tiles.newBuilder()
        .withCharacter('@')
        .withForegroundColor(GameColors.doorFgMadness)
        .withBackgroundColor(GameColors.floorBgMadness)
        .buildCharacterTile()

    val zombie = Tiles.newBuilder()
        .withCharacter('z')
        .withForegroundColor(GameColors.zombieFg)
        .withBackgroundColor(GameColors.zombieBg)
        .buildCharacterTile()

    val zombieMadness = Tiles.newBuilder()
        .withCharacter('z')
        .withForegroundColor(GameColors.doorFgMadness)
        .withBackgroundColor(GameColors.floorBgMadness)
        .buildCharacterTile()

    val torchItem = Tiles.newBuilder()
        .withCharacter('t')
        .withForegroundColor(GameColors.torchItemFg)
        .withBackgroundColor(GameColors.floor1Bg)
        .buildCharacterTile()

    val torch = GameColors.torchFgColors.map {
        Tiles.newBuilder()
            .withCharacter('T')
            .withForegroundColor(it)
            .withBackgroundColor(GameColors.floor1Bg)
            .buildCharacterTile()
    }

    val torchMadness = Tiles.newBuilder()
        .withCharacter('T')
        .withForegroundColor(GameColors.doorFgMadness)
        .withBackgroundColor(GameColors.floorBgMadness)
        .buildCharacterTile()

}
