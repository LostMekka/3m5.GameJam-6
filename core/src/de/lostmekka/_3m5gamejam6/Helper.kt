package de.lostmekka._3m5gamejam6

import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.impl.Position3D
import kotlin.math.abs
import kotlin.random.Random

fun Position.to3DPosition() = toPosition3D(0)

infix fun Position3D.squaredDistanceTo(other: Position3D) =
    (x - other.x).squared + (y - other.y).squared + (z - other.z).squared

infix fun Position3D.hammingDistanceTo(other: Position3D) =
    abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

val Position3D.neighbours
    get() = listOf(
        withRelativeX(1),
        withRelativeY(1),
        withRelativeX(-1),
        withRelativeY(-1)
    )


val Int.squared get() = this * this

fun Random.nextBoolean(probability: Double) = nextDouble() < probability
