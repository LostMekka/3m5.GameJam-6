package de.lostmekka._3m5gamejam6

import com.badlogic.gdx.utils.compression.lzma.Base
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.UIEvent

data class GameContext(
    val world: World,
    val screen: Screen,
    val uiEvent: UIEvent,
    val player: GameEntity<Player>
) : Context {

    fun doSomething() {
        // do something
    }
}

object Player : BaseEntityType(
    name = "Player"
)

object  TorchItem : BaseEntityType(
    name = "Torch Item"
)