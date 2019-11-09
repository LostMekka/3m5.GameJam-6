package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.extensions.onKeyboardEvent
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.api.uievent.KeyboardEventType
import org.hexworks.zircon.api.uievent.Processed


class GameView : BaseView() {
    override fun onDock() {
        val world = World(
            GameConfig.worldSize,
            GameConfig.worldSize
        )

        val sidebar = Components.panel()
            .withSize(GameConfig.sidebarWidth, GameConfig.windowHeight)
            .withAlignmentWithin(screen, ComponentAlignment.RIGHT_CENTER)
            .wrapWithBox()
            .build()

        val gameComponent = GameComponents.newGameComponentBuilder<Tile, GameBlock>()
            .withGameArea(world.gameArea)
            .withVisibleSize(world.gameArea.visibleSize())
            .withProjectionMode(ProjectionMode.TOP_DOWN)
            .withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT)
            .build()

        screen.addComponent(sidebar)
        screen.addComponent(gameComponent)

        screen.onKeyboardEvent(KeyboardEventType.KEY_PRESSED) { event, _ ->
            world.update(screen, event)
            Processed
        }
    }
}
