package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.*;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.screen.Screen;
import org.hexworks.zircon.api.component.ComponentAlignment.CENTER
import org.hexworks.zircon.api.uievent.ComponentEventType.ACTIVATED
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.internal.application.SwingApplication
import org.lwjgl.input.Keyboard.KEY_A
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.onMouseEvent
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.uievent.*


class GameView : BaseView() {
    override fun onDock() {




        val sidebar = Components.panel()
            .withSize(GameConfig.SIDEBAR_WIDTH, GameConfig.WINDOW_HEIGHT-2)
            .wrapWithBox()
            .withTitle("Game Info")
            .withPosition(Positions.offset1x1())
            .build()

        val header = Components.header()
            .withPosition(Positions.offset1x1())
            .withText("Header")
            .build()

        sidebar.addComponent(header)
        screen.addComponent(sidebar)

        screen.onMouseEvent(MouseEventType.MOUSE_MOVED) { event: MouseEvent, phase: UIEventPhase ->
            header.text = event.position.toString()
            UIEventResponses.processed()
        }


    }
}
