package de.lostmekka._3m5gamejam6

import de.lostmekka._3m5gamejam6.config.GameConfig
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.extensions.onMouseEvent
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.api.uievent.MouseEvent
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.UIEventPhase
import kotlin.system.exitProcess

class LoseView(private val causeOfDeath: String) : BaseView() {
    override val theme = GameConfig.theme

    override fun onDock() {
        val msg = "Game Over"
        val header = Components.textBox()
            .withContentWidth(30)
            .addHeader(msg)
            .withAlignmentWithin(screen, ComponentAlignment.CENTER)
            .build()
        val restartButton = Components.button()
            .withAlignmentAround(header, ComponentAlignment.BOTTOM_LEFT)
            .withText("Restart")
            .wrapSides(false)
            .wrapWithBox()
            .withBoxType(BoxType.SINGLE)
            .build()
        val exitButton = Components.button()
            .withAlignmentAround(header, ComponentAlignment.BOTTOM_RIGHT)
            .withText("Quit")
            .wrapSides(false)
            .wrapWithBox()
            .withBoxType(BoxType.SINGLE)
            .build()

        restartButton.onMouseEvent(MouseEventType.MOUSE_RELEASED) { _: MouseEvent, _: UIEventPhase ->
            replaceWith(GameView())
            UIEventResponses.processed()
        }

        exitButton.onMouseEvent(MouseEventType.MOUSE_RELEASED) {event: MouseEvent, _: UIEventPhase ->
            exitProcess(0)
        }

        screen.addComponent(header)
        screen.addComponent(restartButton)
        screen.addComponent(exitButton)
    }
}