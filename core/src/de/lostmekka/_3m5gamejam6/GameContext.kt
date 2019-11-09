package de.lostmekka._3m5gamejam6

import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.UIEvent

data class GameContext(
    val world: World,
    val screen: Screen,
    val uiEvent: UIEvent,
    val player: GameEntity<Player>
) : Context

object Player : BaseEntityType("Player")
object TorchItem : BaseEntityType("Torch Item")
object Torch : BaseEntityType("Torch")
