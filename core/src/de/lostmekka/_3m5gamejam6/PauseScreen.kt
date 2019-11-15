package de.lostmekka._3m5gamejam6

import de.lostmekka._3m5gamejam6.config.gameConfig
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
                container.applyColorTheme(gameConfig.theme)
            }
    }
}

class PauseDialog(screen: Screen) : Dialog(screen) {
    override val container = Components.vbox()
        .withTitle("Pause")
        .withSize(60, 40)
        .withBoxType(BoxType.TOP_BOTTOM_DOUBLE)
        .wrapWithBox()
        .build().apply {
            addComponent(
                Components.textBox()
                    .withContentWidth(56)
                    .withAlignmentWithin(this, ComponentAlignment.CENTER)
                    .addParagraph("Move with WASD")
                    .addParagraph("Press Space to wait")
                    .addParagraph("Press G to grab torches")
                    .addParagraph("Press T repeatedly to place torches")
                    .addParagraph("Press E repeatedly to activate altars")
                    .addParagraph("Activate all altars to unlock the portal")
                    .addParagraph("Press 1/2 to equip your torch/sword")
                    .addParagraph("Run into enemies with your sword to damage them")
                    .addParagraph("Click \"OK\" to continue")
            )
        }
}

fun Screen.storyModal(title: String, vararg paragraphs: String) {
    openModal(StoryDialog(this, title, *paragraphs))
}
class StoryDialog(screen: Screen, title: String, vararg paragraphs: String) : Dialog(screen) {
    override val container = Components.vbox()
        .withTitle(title)
        .withSize(60, 40)
        .withBoxType(BoxType.TOP_BOTTOM_DOUBLE)
        .wrapWithBox()
        .build().apply {
            addComponent(
                Components.textBox()
                    .withContentWidth(56)
                    .withAlignmentWithin(this, ComponentAlignment.CENTER)
                    .apply { for (text in paragraphs) addParagraph(text) }
            )
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
