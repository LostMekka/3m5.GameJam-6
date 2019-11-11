package de.lostmekka._3m5gamejam6.world

import org.hexworks.cobalt.events.api.Event

data class PlayerDied(val cause: String) : Event

data class NextLevel(val depth: Int) : Event

object WON : Event

object ValidInput : Event

data class SoundEvent(val cause: String) : Event

data class MadnessExpanse(val percentage: Int) : Event
