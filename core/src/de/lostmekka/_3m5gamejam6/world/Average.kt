package de.lostmekka._3m5gamejam6.world

import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.game.Cell3D

object GaussKernels {
    // http://dev.theomader.com/gaussian-kernel-calculator/
    val small = listOf(0.06136, 0.24477, 0.38774, 0.24477, 0.06136)
    val medium = listOf(0.071303, 0.131514, 0.189879, 0.214607, 0.189879, 0.131514, 0.071303)
    val big = listOf(
        0.055037,
        0.072806,
        0.090506,
        0.105726,
        0.116061,
        0.119726,
        0.116061,
        0.105726,
        0.090506,
        0.072806,
        0.055037
    )
}

fun World.calculateAverage(
    blocks: Iterable<Cell3D<Tile, GameBlock>>,
    kernel: List<Double>,
    valueGetter: (Position3D) -> Double,
    averageValueSetter: (Position3D, Double) -> Unit
) {
    val kernelOffset = kernel.size / 2
    val averageBuffer = mutableMapOf<Position3D, Double>()
    for ((pos, _) in blocks) {
        var average = 0.0
        kernel.forEachIndexed { i, kernelValue ->
            val localValue = valueGetter(pos.withRelativeX(i - kernelOffset))
            average += localValue * kernelValue
        }
        averageBuffer[pos] = average
    }
    for ((pos, _) in blocks) {
        var average = averageBuffer[pos] ?: 0.0
        kernel.forEachIndexed { i, kernelValue ->
            val localValue = averageBuffer[pos.withRelativeY(i - kernelOffset)] ?: 0.0
            average += localValue * kernelValue
        }
        averageValueSetter(pos, average)
    }
}

