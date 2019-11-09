package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.mvc.base.BaseView


class GameView : BaseView() {
    override fun onDock() {
        val sidebar = Components.panel()
            .withSize(GameConfig.SIDEBAR_WIDTH, GameConfig.WINDOW_HEIGHT)
            .wrapWithBox()
            .build()
        screen.addComponent(sidebar)
    }
}
