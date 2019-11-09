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
import org.hexworks.zircon.api.extensions.onKeyboardEvent
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.internal.application.SwingApplication
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.onMouseEvent
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.component.ComponentAlignment.BOTTOM_LEFT
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.uievent.*


class GameView : BaseView() {
    override fun onDock() {
        val world = World(
            GameConfig.worldSize,
            GameConfig.worldSize
        )
        world.generateRooms()
        world.placePlayer()

        val sidebar = Components.panel()
            .withSize(GameConfig.sidebarWidth, GameConfig.windowHeight)
            .withAlignmentWithin(screen, ComponentAlignment.RIGHT_CENTER)
            .wrapWithBox()
            .withTitle("Game Info")
            .build()

        val mainArea = GameComponents.newGameComponentBuilder<Tile, GameBlock>()
            .withGameArea(world.gameArea)
            .withVisibleSize(world.gameArea.visibleSize())
            .withProjectionMode(ProjectionMode.TOP_DOWN)
            .withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT)
            .build()


        val txtPosition = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .build()

        val txtPointingItem = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, 2)
            .build()

        val logArea = Components.logArea()
            .withTitle("Log")
            .wrapWithBox()
            .withSize(GameConfig.windowWidth - GameConfig.sidebarWidth, GameConfig.logareaHeight)
            .withAlignmentWithin(screen, BOTTOM_LEFT)
            .build()


        sidebar.addComponent(txtPosition)
        sidebar.addComponent(txtPointingItem)
        screen.addComponent(sidebar)
        screen.addComponent(mainArea)

        if (GameConfig.isDebug) {
            screen.addComponent(logArea)
        }

        mainArea.onMouseEvent(MouseEventType.MOUSE_MOVED) { event: MouseEvent, phase: UIEventPhase ->
            txtPosition.text = "Mouse: " + event.position.x.toString() + " | " + event.position.y.toString()

            val temp =
                world.gameArea.fetchBlockOrDefault(Position3D.create(event.position.x, event.position.y, 0))
            txtPointingItem.text = "Pointing at: " + temp.name;


            UIEventResponses.processed()
        }

        screen.onKeyboardEvent(KeyboardEventType.KEY_PRESSED) { event, _ ->
            world.update(screen, event)
            Processed
        }
    }
}
