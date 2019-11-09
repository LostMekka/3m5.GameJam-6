package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.mvc.base.BaseView


class GameView : BaseView() {
    override fun onDock() {
        val world = World(
            GameConfig.worldSize,
            GameConfig.worldSize
        )

        val sidebar = Components.panel()
            .withSize(GameConfig.sidebarWidth, GameConfig.windowHeight)
            .wrapWithBox()
            .build()

        val gameComponent = GameComponents.newGameComponentBuilder<Tile, GameBlock>()
            .withGameArea(world.gameArea)
            .withVisibleSize(world.gameArea.visibleSize())
            .withProjectionMode(ProjectionMode.TOP_DOWN)
            .withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT)
            .build()

        screen.addComponent(sidebar)
        screen.addComponent(gameComponent)
    }
}
