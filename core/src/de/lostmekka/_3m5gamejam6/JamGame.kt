package de.lostmekka._3m5gamejam6

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.world.SoundEvent
import ktx.app.KtxGame
import ktx.app.KtxScreen
import org.hexworks.cobalt.events.api.subscribe
import org.hexworks.zircon.api.AppConfigs
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.application.Application
import org.hexworks.zircon.internal.Zircon


class JamGame : KtxGame<Screen>() {
    override fun create() {
        addScreen(FakeScreen())
        setScreen<FakeScreen>()
    }
}

class FakeScreen : KtxScreen {
    private var batch = SpriteBatch()
    //    private var img = Texture("badlogic.jpg")
    private val application: Application

    init {
        val config = AppConfigs.newConfig()
            .enableBetaFeatures()
            .withDefaultTileset(GameConfig.tileSet)
            .withSize(Sizes.create(GameConfig.windowWidth, GameConfig.windowHeight))
            .build()
        application = SwingApplications.startApplication(config)
        application.dock(StartView())
        val doorSound = Gdx.audio.newSound(Gdx.files.internal("door.wav"))
        val stepSound = Gdx.audio.newSound(Gdx.files.internal("step.wav"))
        val nextLevelSound = Gdx.audio.newSound(Gdx.files.internal("nextLevel.wav"))
        val hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"))
        Zircon.eventBus.subscribe<SoundEvent> {
            when(it.cause) {
                "Door" -> doorSound.play()
                "Step" -> stepSound.play()
                "NextLevel" -> nextLevelSound.play()
                "Hit" -> hitSound.play()
            }
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
//        batch.draw(img, 0f, 0f)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
//        img.dispose()
    }
}
