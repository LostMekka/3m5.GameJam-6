package de.lostmekka._3m5gamejam6

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.world.MadnessExpanse
import de.lostmekka._3m5gamejam6.world.SoundEvent
import de.lostmekka._3m5gamejam6.world.SoundEventType
import ktx.app.KtxGame
import ktx.app.KtxScreen
import org.hexworks.cobalt.events.api.subscribe
import org.hexworks.zircon.api.AppConfigs
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.application.Application
import org.hexworks.zircon.internal.Zircon
import kotlin.random.Random


class JamGame : KtxGame<Screen>() {
    override fun create() {
        addScreen(FakeScreen())
        setScreen<FakeScreen>()
    }
}

class FakeScreen : KtxScreen {
    private var batch = SpriteBatch()
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
        val backgroundMusic = music("sound/music.ogg")
        backgroundMusic.isLooping = true
        backgroundMusic.play()
        backgroundMusic.volume = GameConfig.backgroundMusicVolume

        // start madness whispering
        val madnessWhisper = music("sound/madness.ogg")
        madnessWhisper.isLooping = true
        madnessWhisper.play()
        madnessWhisper.volume = GameConfig.whisperVolumeMin

        // add sound effects
        val doorSound = sound("sound/door.wav")
        val stepSound = sound("sound/step.wav")
        val nextLevelSound = sound("sound/nextLevel.wav")
        val hitSound = sound("sound/hit.wav")
        val madnessHitSound = sound("sound/madnessHit.wav")
        val pain1Sound = sound("sound/pain1.wav")
        val pain2Sound = sound("sound/pain2.wav")
        val buildProgressSound = sound("sound/build_progress.wav")
        val buildFinishedSound = sound("sound/basedrum.wav")
        Zircon.eventBus.subscribe<SoundEvent> {
            when (it.cause) {
                SoundEventType.Door -> doorSound.play()
                SoundEventType.Step -> stepSound.play(0.5f)
                SoundEventType.NextLevel -> nextLevelSound.play()
                SoundEventType.ZombieHit -> hitSound.play()
                SoundEventType.PlayerHit -> pain1Sound.play()
                SoundEventType.PlayerDeath -> pain2Sound.play()
                SoundEventType.MadnessHit -> madnessHitSound.play(0.9f, 1 + 0.25f * (2 * Random.nextFloat() - 1), 0f)
                SoundEventType.BuildProgress -> buildProgressSound.play()
                SoundEventType.BuildFinished -> buildFinishedSound.play()
            }
        }

        // calculate madness whispering volume
        Zircon.eventBus.subscribe<MadnessExpanse> {
            madnessWhisper.volume = GameConfig.whisperVolumeMin +
                    ((it.percentage).toFloat() / 100 * (GameConfig.whisperVolumeMax - GameConfig.whisperVolumeMin))
        }
    }

    private fun music(path: String) = Gdx.audio.newMusic(Gdx.files.internal(path))
    private fun sound(path: String) = Gdx.audio.newSound(Gdx.files.internal(path))

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }
}
