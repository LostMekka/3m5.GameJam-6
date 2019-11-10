package de.lostmekka._3m5gamejam6

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.world.MadnessExpanse
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

        // start background music
        var backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/music.ogg"))
        backgroundMusic.isLooping = true
        backgroundMusic.play()
        backgroundMusic.volume = GameConfig.backgroundMusicVolume

        // start madness whispering
        var madnessWhisper = Gdx.audio.newMusic(Gdx.files.internal("sound/madness.ogg"))
        madnessWhisper.isLooping = true
        madnessWhisper.play()
        madnessWhisper.volume = GameConfig.whisperVolumeMin

        // add sound effects
        val doorSound = Gdx.audio.newSound(Gdx.files.internal("sound/door.wav"))
        val stepSound = Gdx.audio.newSound(Gdx.files.internal("sound/step.wav"))
        val nextLevelSound = Gdx.audio.newSound(Gdx.files.internal("sound/nextLevel.wav"))
        val hitSound = Gdx.audio.newSound(Gdx.files.internal("sound/hit.wav"))
        Zircon.eventBus.subscribe<SoundEvent> {
            when(it.cause) {
                "Door" -> doorSound.play()
                "Step" -> stepSound.play()
                "NextLevel" -> nextLevelSound.play()
                "Hit" -> hitSound.play()
            }
        }

        // calculate madness whispering volume
        Zircon.eventBus.subscribe<MadnessExpanse> {
            madnessWhisper.volume = GameConfig.whisperVolumeMin +
                    ((it.percentage).toFloat() / 100 * (GameConfig.whisperVolumeMax - GameConfig.whisperVolumeMin))
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
