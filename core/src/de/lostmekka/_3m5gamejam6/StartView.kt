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

class StartView : BaseView() {

    override val theme = GameConfig.theme

    override fun onDock() {
        val msg = "Welcome to Darkness Follows."
        val header = Components.textBox() // a text box can hold headers, paragraphs and list items
            .withContentWidth(msg.length) // the width of the content of this text box
            .addHeader(msg) // we add a header
            .addNewLine() // and a new line
            .withAlignmentWithin(screen, ComponentAlignment.CENTER) // and align it to center
            .build() // finally we build the component
        val startButton = Components.button()
            // we align the button to the bottom center of our header
            .withAlignmentAround(header, ComponentAlignment.BOTTOM_CENTER)
            .withText("Start!") // its text is "Start!"
            .wrapSides(false) // we don't want to wrap this button with [ and ]
            .withBoxType(BoxType.SINGLE) // but we want a box around it
            .wrapWithShadow() // and some shadow
            .wrapWithBox()
            .build()
        screen.addComponent(header)
        screen.addComponent(startButton)

        startButton.onMouseEvent(MouseEventType.MOUSE_RELEASED) { _: MouseEvent, _: UIEventPhase ->
            replaceWith(GameView())
            UIEventResponses.processed()
        }
    }

}