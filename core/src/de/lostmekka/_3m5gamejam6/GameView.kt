package de.lostmekka._3m5gamejam6

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.world.GameBlock
import de.lostmekka._3m5gamejam6.world.World
import de.lostmekka._3m5gamejam6.world.generateMadness
import de.lostmekka._3m5gamejam6.world.generateRooms
import de.lostmekka._3m5gamejam6.world.generateTorchItems
import de.lostmekka._3m5gamejam6.world.placePlayer
import de.lostmekka._3m5gamejam6.world.placeTorch
import de.lostmekka._3m5gamejam6.world.placeTorchItem
import de.lostmekka._3m5gamejam6.world.updateLighting
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.ComponentAlignment.BOTTOM_LEFT
import org.hexworks.zircon.api.component.Visibility
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.extensions.onKeyboardEvent
import org.hexworks.zircon.api.extensions.onMouseEvent
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.api.uievent.KeyboardEventType
import org.hexworks.zircon.api.uievent.MouseEvent
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.api.uievent.UIEventPhase


class GameView : BaseView() {
    override fun onDock() {
        val world = World(
            GameConfig.worldSize,
            GameConfig.worldSize
        )
        world.generateRooms()
        world.placePlayer()
        world.generateTorchItems()
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

        val txtPointingLabel = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, 3)
            .build()

        val txtPointingItem = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, 4)
            .build()

        val txtHealth = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, 5)
            .build()

        val txtTorches = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, 7)
            .build()

        val txtTorchBuildProgress = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, 9)
            .build()

        val logArea = Components.logArea()
            .withTitle("Log")
            .wrapWithBox()
            .withSize(GameConfig.windowWidth - GameConfig.sidebarWidth, GameConfig.logAreaHeight)
            .withAlignmentWithin(screen, BOTTOM_LEFT)
            .build()

        sidebar.addComponent(txtPosition)
        sidebar.addComponent(txtPointingLabel)
        sidebar.addComponent(txtPointingItem)
        sidebar.addComponent(txtHealth)
        sidebar.addComponent(txtTorches)
        sidebar.addComponent(txtTorchBuildProgress)

        screen.addComponent(sidebar)
        screen.addComponent(mainArea)

        if (GameConfig.enableDebugLogArea) {
            screen.addComponent(logArea)
        }

        mainArea.onMouseEvent(MouseEventType.MOUSE_MOVED) { event: MouseEvent, _: UIEventPhase ->
            txtPosition.text = "Mouse: ${event.position.x} | ${event.position.y}"

            val temp = world.gameArea.fetchBlockOrDefault(Position3D.create(event.position.x, event.position.y, 0))
            val item = if (temp.isLit) temp.name else "Darkness"
            txtPointingLabel.text = "Looking at: "
            if (item.length < 10) {
                txtPointingLabel.text += item
                txtPointingItem.text = " "
            } else txtPointingItem.text = "  $item"

            UIEventResponses.processed()
        }

        mainArea.onMouseEvent(MouseEventType.MOUSE_CLICKED) { event: MouseEvent, _: UIEventPhase ->
            if (GameConfig.enableDebugTorchPlacement) {
                if (event.button == 1) {
                    world.placeTorchItem(event.position.to3DPosition())
                } else {
                    world.placeTorch(event.position.to3DPosition())
                }
            }
            UIEventResponses.processed()
        }

        screen.onKeyboardEvent(KeyboardEventType.KEY_PRESSED) { event, _ ->
            world.onKeyInput(screen, event)
            txtHealth.text = "HP: " + world.player.health
            txtTorches.text = "Torches: " + world.player.inventory.torches
            if (world.player.inventory.torchBuildingProgress > 0) {
                txtTorchBuildProgress.isVisible = Visibility.Visible
                txtTorchBuildProgress.text = "Torch Build: " + getTorchBuildingProgressBar(world)
            } else txtTorchBuildProgress.isVisible = Visibility.Hidden
            Processed
        }
    }

    private fun getTorchBuildingProgressBar(world: World): String {
        var progressBar = "["
        for (i in 0..4) progressBar += if (i < world.player.inventory.torchBuildingProgress) "=" else " "
        progressBar += "]"
        return progressBar
    }
}
