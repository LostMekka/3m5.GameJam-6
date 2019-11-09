package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.graphics.Symbols

object GameTileRepository {

    val EMPTY: CharacterTile = Tiles.empty()

    val SHADOW: CharacterTile = Tiles.newBuilder()
        .withCharacter(' ')
        .withForegroundColor(GameColors.shadowFG)
        .withBackgroundColor(GameColors.shadowBG)
        .buildCharacterTile()

    val FLOOR: CharacterTile = Tiles.newBuilder()
        .withCharacter(Symbols.INTERPUNCT)
        .withForegroundColor(GameColors.FLOOR_FOREGROUND)
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .buildCharacterTile()

    val FLOOR_MADNESS: CharacterTile = Tiles.newBuilder()
        .withCharacter(Symbols.INTERPUNCT)
        .withForegroundColor(GameColors.FLOOR_FOREGROUND_MADNESS)
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND_MADNESS)
        .buildCharacterTile()

    val WALL: CharacterTile = Tiles.newBuilder()
        .withCharacter('#')
        .withForegroundColor(GameColors.WALL_FOREGROUND)
        .withBackgroundColor(GameColors.WALL_BACKGROUND)
        .buildCharacterTile()

    val WALL_MADNESS: CharacterTile = Tiles.newBuilder()
        .withCharacter('#')
        .withForegroundColor(GameColors.WALL_FOREGROUND_MADNESS)
        .withBackgroundColor(GameColors.WALL_BACKGROUND_MADNESS)
        .buildCharacterTile()

    val DOOR: CharacterTile = Tiles.newBuilder()
        .withCharacter('H')
        .withForegroundColor(GameColors.ACCENT_COLOR_2)
        .withBackgroundColor(GameColors.WALL_BACKGROUND)
        .buildCharacterTile()

    val DOOR_MADNESS: CharacterTile = Tiles.newBuilder()
        .withCharacter('H')
        .withForegroundColor(GameColors.ACCENT_COLOR_2_MADNESS)
        .withBackgroundColor(GameColors.WALL_BACKGROUND_MADNESS)
        .buildCharacterTile()

    val PLAYER = Tiles.newBuilder()
        .withCharacter('@')
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .withForegroundColor(GameColors.ACCENT_COLOR)
        .buildCharacterTile()

    val PLAYER_MADNESS = Tiles.newBuilder()
        .withCharacter('@')
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND_MADNESS)
        .withForegroundColor(GameColors.ACCENT_COLOR_MADNESS)
        .buildCharacterTile()

    val TORCH_ITEM = Tiles.newBuilder()
        .withCharacter('t')
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .withForegroundColor(GameColors.TORCH_COLOR_0)
        .buildCharacterTile()

    val TORCH = Tiles.newBuilder()
        .withCharacter('T')
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .withForegroundColor(GameColors.TORCH_COLOR_0)
        .buildCharacterTile()

}
