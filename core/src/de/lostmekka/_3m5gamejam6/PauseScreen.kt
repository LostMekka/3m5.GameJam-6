package de.lostmekka._3m5gamejam6

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.GameEntity
import de.lostmekka._3m5gamejam6.entity.Player
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.builder.component.ModalBuilder
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Container
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.component.modal.Modal
import org.hexworks.zircon.api.component.modal.ModalFragment
import org.hexworks.zircon.api.extensions.onComponentEvent
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.ComponentEventType
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.internal.component.modal.EmptyModalResult

abstract class Dialog(
    private val screen: Screen
) : ModalFragment<EmptyModalResult> {

    abstract val container: Container

    final override val root: Modal<EmptyModalResult> by lazy {
        ModalBuilder.newBuilder<EmptyModalResult>()
            .withComponent(container)
            .withParentSize(screen.size)
            .withCenteredDialog(true)
            .build().also {
                container.addFragment(CloseButtonFragment(it, container))
                container.applyColorTheme(GameConfig.theme)
            }
    }
}

class PauseDialog(screen: Screen) : Dialog(screen) {

    override val container = Components.vbox()
        .withTitle("Pause")
        .withSize(30, 15)
        .withBoxType(BoxType.TOP_BOTTOM_DOUBLE)
        .wrapWithBox()
        .build().apply {
            addComponent(Components.textBox()
                .withContentWidth(27)
                .addParagraph("Click \"OK\" to continue"))
        }
}

class CloseButtonFragment(modal: Modal<EmptyModalResult>, parent: Container) : Fragment {

    override val root = Components.button().withText("OK")
        .withAlignmentWithin(parent, ComponentAlignment.CENTER)
        .build().apply {
            onComponentEvent(ComponentEventType.ACTIVATED) {
                modal.close(EmptyModalResult)
                Processed
            }
        }
}