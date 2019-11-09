package de.lostmekka._3m5gamejam6

import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.world.GameBlock
import de.lostmekka._3m5gamejam6.world.World
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.ComponentAlignment.BOTTOM_LEFT
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.extensions.onKeyboardEvent
import org.hexworks.zircon.api.extensions.onMouseEvent
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.api.uievent.*


class GameView : BaseView() {
    override fun onDock() {
        val world = World(
            GameConfig.worldSize,
            GameConfig.worldSize
        )
        world.generateRooms()
        world.placePlayer()
        world.generateMadness()
        world.updateLighting()

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
            .withPosition(0, 1)
            .build()

        val txtHealth = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0,5)
            .build()

        val txtTorches = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0,7)
            .build()

        val txtPointingItem = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, 3)
            .build()

        val logArea = Components.logArea()
            .withTitle("Log")
            .wrapWithBox()
            .withSize(GameConfig.windowWidth - GameConfig.sidebarWidth, GameConfig.logareaHeight)
            .withAlignmentWithin(screen, BOTTOM_LEFT)
            .build()

        sidebar.addComponent(txtPosition)
        sidebar.addComponent(txtPointingItem)
        sidebar.addComponent(txtHealth)
        sidebar.addComponent(txtTorches)

        screen.addComponent(sidebar)
        screen.addComponent(mainArea)

        if (GameConfig.isDebug) {
            screen.addComponent(logArea)
        }

        mainArea.onMouseEvent(MouseEventType.MOUSE_MOVED) { event: MouseEvent, phase: UIEventPhase ->
            txtPosition.text = "Mouse: " + event.position.x.toString() + " | " + event.position.y.toString()

            val temp =
                world.gameArea.fetchBlockOrDefault(Position3D.create(event.position.x, event.position.y, 0))
            txtPointingItem.text = "Pointing at: " + temp.name


            UIEventResponses.processed()
        }

        mainArea.onMouseEvent(MouseEventType.MOUSE_CLICKED){event: MouseEvent, phase: UIEventPhase ->
            if (event.button == 0) {
                world.placeTorchItem(event.position.to3DPosition())
            } else {
                world.placeTorch(event.position.to3DPosition())
            }
            UIEventResponses.processed()
        }

        screen.onKeyboardEvent(KeyboardEventType.KEY_PRESSED) { event, _ ->
            world.onKeyInput(screen, event)
            txtHealth.text = "HP: " + world.player.health
            txtTorches.text = "Torches: " + world.player.inventory.torches
            Processed
        }
    }
}
