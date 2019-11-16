package de.lostmekka._3m5gamejam6

import de.lostmekka._3m5gamejam6.config.gameConfig
import de.lostmekka._3m5gamejam6.entity.Enemy
import de.lostmekka._3m5gamejam6.entity.attribute.health
import de.lostmekka._3m5gamejam6.entity.attribute.inventory
import de.lostmekka._3m5gamejam6.world.GameBlock
import de.lostmekka._3m5gamejam6.world.NextLevel
import de.lostmekka._3m5gamejam6.world.PlayerDied
import de.lostmekka._3m5gamejam6.world.ValidInput
import de.lostmekka._3m5gamejam6.world.WON
import de.lostmekka._3m5gamejam6.world.World
import de.lostmekka._3m5gamejam6.world.generateAltars
import de.lostmekka._3m5gamejam6.world.generateEnemies
import de.lostmekka._3m5gamejam6.world.generateMadness
import de.lostmekka._3m5gamejam6.world.generateRooms
import de.lostmekka._3m5gamejam6.world.generateTorchItems
import de.lostmekka._3m5gamejam6.world.placePlayer
import de.lostmekka._3m5gamejam6.world.placePortal
import de.lostmekka._3m5gamejam6.world.placeTorch
import de.lostmekka._3m5gamejam6.world.placeTorchItem
import de.lostmekka._3m5gamejam6.world.updateLighting
import org.hexworks.cobalt.events.api.subscribe
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.UIEventResponses
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.ComponentAlignment.BOTTOM_LEFT
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
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
    private lateinit var sidebar: Panel
    private var nextLabelY = 2
    private val sidebarLabelUpdaters = mutableMapOf<Label, () -> String>()
    private var lastMousePos = Position.create(-1, -1)

    override fun onUndock() {
        isActive = false
    }

    override fun onDock() {
        isActive = true

        val world = World(
            gameConfig.worldSize,
            gameConfig.worldSize,
            levelDepth
        )
        world.generateRooms()
        world.placePlayer()
        world.placePortal()
        world.generateAltars()
        world.generateTorchItems()
        world.generateEnemies()
        world.generateMadness()
        world.updateLighting()

        sidebar = Components.panel()
            .withSize(gameConfig.window.sidebarWidth, gameConfig.window.height)
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

        addSidebarLabel { "Level ${levelDepth + 1}" }
        addSidebarLabel { "${world.activatedAltarCount}/${world.altarCount} Altars" }
        addSidebarBlankLine()

        addSidebarLabel { "HP: ${world.player.health}" }
        addSidebarLabel { "Torches: ${world.player.inventory.torches}" }
        addSidebarLabel { if (world.player.inventory.holdsSword) "Equipped: Sword" else "Equipped: Torch" }
        addSidebarLabel {
            if (world.player.inventory.buildingProgress > 0) {
                "Build: ${getTorchBuildingProgressBar(world)}"
            } else {
                ""
            }
        }
        addSidebarBlankLine()

        addSidebarLabel { "Pointing at:" }
        addSidebarLabel { world[lastMousePos]?.name ?: "????" }
        addSidebarLabel {
            val block = world[lastMousePos]
            if (block == null) {
                "????"
            } else {
                block.firstEntity()
                    ?.takeIf { it.type is Enemy }
                    ?.let { "  HP: ${it.health}" }
                    ?: ""
            }
        }
        addSidebarBlankLine()

        Components.label()
            .withSize(gameConfig.window.sidebarWidth, 1)
            .withPosition(0, sidebar.height - 5)
            .withText("Press esc => Pause")
            .build()
            .also { sidebar.addComponent(it) }

        val logArea = Components.logArea()
            .withTitle("Log")
            .wrapWithBox()
            .withSize(gameConfig.window.width - gameConfig.window.sidebarWidth, gameConfig.window.logAreaHeight)
            .withAlignmentWithin(screen, BOTTOM_LEFT)
            .build()

        screen.addComponent(sidebar)
        screen.addComponent(mainArea)
        if (gameConfig.debug.enableLogArea) screen.addComponent(logArea)

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
            lastMousePos = event.position
            updateLabelTexts()
            UIEventResponses.processed()
        }

        mainArea.onMouseEvent(MouseEventType.MOUSE_CLICKED) { event: MouseEvent, _: UIEventPhase ->
            if (gameConfig.debug.enableTorchPlacement) {
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
                updateLabelTexts()
            }
            Processed
        }

        when (levelDepth) {
            0 -> screen.storyModal(
                "Arrival",
                "I have finally arrived.",
                "As the heavy doors shut behind me, a cold shiver runs down my spine. I can already hear the whispers, trying to lure me into the madness.",
                "But I have to do this. There is no other way. The altars of this place should be able to open the way ahead."
            )
            1 -> screen.storyModal(
                "The Crypt",
                "So far so good. I managed to get past these fiends, but I am sure there will be more where they came from.",
                "This place may be bigger than I thought and I feel the whispers coming through the cracks in the wall, taunting me.",
                "The quicker I get through this temple, the better..."
            )
            2 -> screen.storyModal(
                "The Calling",
                "They are calling me! I was dead sure I heard my name, always from right around the next corner.",
                "Is this place toying with me? Perhaps the darkness is just making me paranoid...",
                "I need to get through, no matter the cost. For her."
            )
            3 -> screen.storyModal(
                "Heat",
                "It is getting warmer; I must be getting closer.",
                "The voices are clearer now. They suggest - no - command me to end this here and now.",
                "But this is not about me. This is not the time for my death."
            )
            4 -> screen.storyModal(
                "Heart of Madness",
                "I saw her in the shadows. Mocking me. How can this be? am I too late? Is this even real?",
                "How did I even get here? This place is feeding on my sanity one thought at a time.",
                "The twisted architecture of this place is getting ever more grotesque. The final portal is near..."
            )
        }
    }

    private fun updateLabelTexts() {
        for ((label, textProvider) in sidebarLabelUpdaters) label.text = textProvider()
    }

    private fun addSidebarLabel(textProvider: (() -> String)? = null): Label = Components.label()
        .withSize(gameConfig.window.sidebarWidth, 1)
        .withPosition(0, nextLabelY++)
        .withText(textProvider?.invoke() ?: "")
        .build()
        .also {
            sidebar.addComponent(it)
            if (textProvider != null) sidebarLabelUpdaters[it] = textProvider
        }

    private fun addSidebarBlankLine(count: Int = 1) {
        nextLabelY += count
    }

    private fun getTorchBuildingProgressBar(world: World): String {
        return (0..world.player.inventory.maxBuildingProgress)
            .joinToString(
                prefix = "[",
                postfix = "]",
                separator = ""
            ) { if (it < world.player.inventory.buildingProgress) "=" else "." }
    }
}
