package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.graphics.Symbols

object GameTileRepository {

    val EMPTY: CharacterTile = Tiles.empty()

    val FLOOR: CharacterTile = Tiles.newBuilder()
        .withCharacter(Symbols.INTERPUNCT)
        .withForegroundColor(GameColors.FLOOR_FOREGROUND)
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .buildCharacterTile()

    val WALL: CharacterTile = Tiles.newBuilder()
        .withCharacter('#')
        .withForegroundColor(GameColors.WALL_FOREGROUND)
        .withBackgroundColor(GameColors.WALL_BACKGROUND)
        .buildCharacterTile()

    val DOOR: CharacterTile = Tiles.newBuilder()
        .withCharacter('H')
        .withForegroundColor(GameColors.ACCENT_COLOR_2)
        .withBackgroundColor(GameColors.WALL_BACKGROUND)
        .buildCharacterTile()

    val PLAYER = Tiles.newBuilder()
        .withCharacter('@')
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .withForegroundColor(GameColors.ACCENT_COLOR)
        .buildCharacterTile()

    val MADNESS = Tiles.newBuilder()
        .withCharacter('M')
        .withBackgroundColor(GameColors.MADNESS)
        .withForegroundColor(GameColors.MADNESS)
        .buildCharacterTile()
}
