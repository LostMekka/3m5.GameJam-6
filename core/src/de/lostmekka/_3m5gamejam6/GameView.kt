package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.*;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.screen.Screen;
import org.hexworks.zircon.api.uievent.KeyCode;
import org.hexworks.zircon.api.uievent.KeyboardEventType;
import org.hexworks.zircon.api.uievent.MouseEventType;
import org.hexworks.zircon.api.uievent.UIEventPhase;

import org.hexworks.zircon.api.component.ComponentAlignment.CENTER
import org.hexworks.zircon.api.uievent.ComponentEventType.ACTIVATED

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.mvc.base.BaseView






class GameView : BaseView() {
    override fun onDock() {


        val tileGrid = SwingApplications.startTileGrid()
        val screen = Screens.createScreenFor(tileGrid)


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
    }
}
