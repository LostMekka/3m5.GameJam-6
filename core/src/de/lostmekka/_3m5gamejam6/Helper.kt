package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.data.Position
import kotlin.random.Random

fun Position.to3DPosition() = toPosition3D(0)

fun Random.nextBoolean(probability: Double) = nextDouble() < probability
