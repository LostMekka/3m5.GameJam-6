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

    val wall: CharacterTile = Tiles.newBuilder()
        .withCharacter('#')
        .withForegroundColor(GameColors.wallFg)
        .withBackgroundColor(GameColors.wallBg)
        .buildCharacterTile()

    val wallMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('#')
        .withForegroundColor(GameColors.wallFgMadness)
        .withBackgroundColor(GameColors.wallBgMadness)
        .buildCharacterTile()

    val door: CharacterTile = Tiles.newBuilder()
        .withCharacter('H')
        .withForegroundColor(GameColors.doorFg)
        .withBackgroundColor(GameColors.wallBg)
        .buildCharacterTile()

    val doorMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('H')
        .withForegroundColor(GameColors.doorFgMadness)
        .withBackgroundColor(GameColors.wallBgMadness)
        .buildCharacterTile()

    val altar: CharacterTile = Tiles.newBuilder()
        .withCharacter('a')
        .withForegroundColor(GameColors.altarFg)
        .withBackgroundColor(GameColors.wallBg)
        .buildCharacterTile()

    val altarMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('a')
        .withForegroundColor(GameColors.altarFgMadness)
        .withBackgroundColor(GameColors.wallBgMadness)
        .buildCharacterTile()

    val altarActivated: CharacterTile = Tiles.newBuilder()
        .withCharacter('A')
        .withForegroundColor(GameColors.altarFgActivated)
        .withBackgroundColor(GameColors.wallBg)
        .buildCharacterTile()

    val stairs: CharacterTile = Tiles.newBuilder()
        .withCharacter('s')
        .withForegroundColor(GameColors.altarFg)
        .withBackgroundColor(GameColors.wallBg)
        .buildCharacterTile()

    val stairsMadness: CharacterTile = Tiles.newBuilder()
        .withCharacter('s')
        .withForegroundColor(GameColors.altarFgMadness)
        .withBackgroundColor(GameColors.wallBgMadness)
        .buildCharacterTile()

    val stairsActivated: CharacterTile = Tiles.newBuilder()
        .withCharacter('S')
        .withForegroundColor(GameColors.altarFgActivated)
        .withBackgroundColor(GameColors.wallBg)
        .buildCharacterTile()

    val player = Tiles.newBuilder()
        .withCharacter('@')
        .withForegroundColor(GameColors.doorFg)
        .withBackgroundColor(GameColors.floor1Bg)
        .buildCharacterTile()

    val enemyZombie = Tiles.newBuilder()
        .withCharacter('z')
        .withBackgroundColor(GameColors.zombieBg)
        .withForegroundColor(GameColors.zombieFg)
        .buildCharacterTile()

    val playerMadness = Tiles.newBuilder()
        .withCharacter('@')
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

}
