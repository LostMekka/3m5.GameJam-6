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

    val floor: CharacterTile = Tiles.newBuilder()
        .withCharacter('.')
        .withForegroundColor(GameColors.floorFg)
        .withBackgroundColor(GameColors.floorBg)
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

    val player = Tiles.newBuilder()
        .withCharacter('@')
        .withForegroundColor(GameColors.doorFg)
        .withBackgroundColor(GameColors.floorBg)
        .buildCharacterTile()

    val playerMadness = Tiles.newBuilder()
        .withCharacter('@')
        .withForegroundColor(GameColors.doorFgMadness)
        .withBackgroundColor(GameColors.floorBgMadness)
        .buildCharacterTile()

    val torchItem = Tiles.newBuilder()
        .withCharacter('t')
        .withForegroundColor(GameColors.torchItemFg)
        .withBackgroundColor(GameColors.floorBg)
        .buildCharacterTile()

    val torch = GameColors.torchFgColors.map {
        Tiles.newBuilder()
            .withCharacter('T')
            .withForegroundColor(it)
            .withBackgroundColor(GameColors.floorBg)
            .buildCharacterTile()
    }

}
