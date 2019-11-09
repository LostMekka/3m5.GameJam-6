package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.*;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.screen.Screen;
import org.hexworks.zircon.api.component.ComponentAlignment.CENTER
import org.hexworks.zircon.api.uievent.ComponentEventType.ACTIVATED
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.internal.application.SwingApplication
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.onMouseEvent
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.component.ComponentAlignment.BOTTOM_RIGHT
import org.hexworks.zircon.api.uievent.*


class GameView : BaseView() {
    override fun onDock() {
        val world = World(
            GameConfig.worldSize,
            GameConfig.worldSize
        )

        val sidebar = Components.panel()
            .withSize(GameConfig.sidebarWidth, GameConfig.windowHeight)
            .withAlignmentWithin(screen, ComponentAlignment.RIGHT_CENTER)
            .withPosition(position = positions)
            .wrapWithBox()
            .withTitle("Game Info")
            .build()

        val gameComponent = GameComponents.newGameComponentBuilder<Tile, GameBlock>()
            .withGameArea(world.gameArea)
            .withVisibleSize(world.gameArea.visibleSize())
            .withProjectionMode(ProjectionMode.TOP_DOWN)
            .withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT)
            .build()


        val header = Components.header()
            .withText("Header")
            .build()

        val logArea = Components.logArea()
            .withTitle("Log")
            .wrapWithBox()
            .withSize(GameConfig.windowWidth - GameConfig.sidebarWidth, GameConfig.logareaHeight)
            .withAlignmentWithin(screen, BOTTOM_RIGHT)
            .build()


        sidebar.addComponent(header)
        screen.addComponent(sidebar)
        screen.addComponent(gameComponent)

        if (GameConfig.isDebug) {
            screen.addComponent(logArea)

        }


        screen.onMouseEvent(MouseEventType.MOUSE_MOVED) { event: MouseEvent, phase: UIEventPhase ->
            header.text = event.position.toString()
            println("YESSSS SIRRRRRR")
            UIEventResponses.processed()
        }


    }
}
