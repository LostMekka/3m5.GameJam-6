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


class GameView : BaseView() {
    override fun onDock() {
        val world = World(
            GameConfig.worldSize,
            GameConfig.worldSize
        )
        world.generateRooms()

        val sidebar = Components.panel()
            .withSize(GameConfig.sidebarWidth, GameConfig.windowHeight - 2)
            .withAlignmentWithin(screen, ComponentAlignment.RIGHT_CENTER)
            .wrapWithBox()
            .withTitle("Game Info")
            .withPosition(Positions.offset1x1())
            .build()

        val gameComponent = GameComponents.newGameComponentBuilder<Tile, GameBlock>()
            .withGameArea(world.gameArea)
            .withVisibleSize(world.gameArea.visibleSize())
            .withProjectionMode(ProjectionMode.TOP_DOWN)
            .withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT)
            .build()


        val header = Components.header()
            .withPosition(Positions.offset1x1())
            .withText("Header")
            .build()

        sidebar.addComponent(header)
        screen.addComponent(sidebar)
        screen.addComponent(gameComponent)

        screen.onMouseEvent(MouseEventType.MOUSE_MOVED) { event: MouseEvent, phase: UIEventPhase ->
            header.text = event.position.toString()
            UIEventResponses.processed()
        }



        screen.onKeyboardEvent(KeyboardEventType.KEY_PRESSED) { event, _ ->
            world.update(screen, event)
            Processed
        }
    }
}
