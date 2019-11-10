package de.lostmekka._3m5gamejam6

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.world.*
import org.hexworks.cobalt.events.api.subscribe
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.ComponentAlignment.BOTTOM_LEFT
import org.hexworks.zircon.api.component.Visibility
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.extensions.onKeyboardEvent
import org.hexworks.zircon.api.extensions.onMouseEvent
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.KeyboardEventType
import org.hexworks.zircon.api.uievent.MouseEvent
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.api.uievent.UIEventPhase
import org.hexworks.zircon.internal.Zircon

class GameView(private val levelDepth: Int = 0) : BaseView() {

    private var isActive = false

    override fun onUndock() {
        isActive = false
    }

    override fun onDock() {
        isActive = true

        val world = World(
            GameConfig.worldSize,
            GameConfig.worldSize,
            levelDepth
        )
        world.generateRooms()
        world.placePlayer()
        world.placeStairs()
        world.generateAltars()
        world.generateTorchItems()
        world.generateEnemies()
        world.generateMadness()
        world.updateLighting()


        var index = 1

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
            .withPosition(0, index)
            .build()

        index+=2

        val txtPointingLabel = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, index)
            .build()
        index+=2

        val txtPointingItem = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, index)
            .build()
        index+=2

        val txtHealth = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, index)
            .build()
        index+=2

        val txtTorches = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, index)
            .build()
        index+=2

        val txtEquipment = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, index)
            .build()
        index+=2

        val txtTorchBuildProgress = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, index)
            .build()
        index+=2

        val txtTutotialInfo = Components.label()
            .withSize(GameConfig.sidebarWidth, 1)
            .withPosition(0, sidebar.height-5)
            .withText("Press esc => Pause")
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
        sidebar.addComponent(txtEquipment)
        sidebar.addComponent(txtTorchBuildProgress)
        sidebar.addComponent(txtTutotialInfo)

        screen.addComponent(sidebar)
        screen.addComponent(mainArea)

        if (GameConfig.enableDebugLogArea) {
            screen.addComponent(logArea)
        }

        Zircon.eventBus.subscribe<PlayerDied> {
            if (isActive) {
                replaceWith(LoseView(it.cause))
                close()
            }
        }

        Zircon.eventBus.subscribe<ValidInput> {
            if (isActive) world.tick()
        }

        Zircon.eventBus.subscribe<NextLevel> {
            if (isActive) {
                replaceWith(GameView(levelDepth = it.depth))
                close()
            }
        }

        Zircon.eventBus.subscribe<WON> {
            if (isActive) {
                replaceWith(WinView())
                close()
            }
        }

        mainArea.onMouseEvent(MouseEventType.MOUSE_MOVED) { event: MouseEvent, _: UIEventPhase ->
            txtPosition.text = "Mouse: ${event.position.x} | ${event.position.y}"

            val item = world[event.position]?.name ?: "????"
            txtPointingLabel.text = "Looking at: "
            if (item.length < 10) {
                txtPointingLabel.text += item
                txtPointingItem.text = " "
            } else {
                txtPointingItem.text = "  $item"
            }

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
            if (event.code == KeyCode.ESCAPE) {
                screen.openModal(PauseDialog(screen))
            } else {
                world.onKeyInput(screen, event)
                txtHealth.text = "HP: " + world.player.health
                txtTorches.text = "Torches: " + world.player.inventory.torches

                if (world.player.inventory.holdsSword)
                    txtEquipment.text = "Equiped: Sword"
                else txtEquipment.text = "Equiped: Torch"

                if (world.player.inventory.buildingProgress > 0) {
                    txtTorchBuildProgress.isVisible = Visibility.Visible
                    txtTorchBuildProgress.text = "Build: " + getTorchBuildingProgressBar(world)
                } else {
                    txtTorchBuildProgress.isVisible = Visibility.Hidden
                }
            }
            Processed
        }
    }

    private fun getTorchBuildingProgressBar(world: World): String {
        return (0 until world.player.inventory.maxBuildingProgress)
            .joinToString(
                prefix = "[",
                postfix = "]",
                separator = ""
            ) { if (it < world.player.inventory.buildingProgress) "=" else "." }
    }
}
