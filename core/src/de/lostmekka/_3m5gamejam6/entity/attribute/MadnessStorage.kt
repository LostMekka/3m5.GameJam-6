package de.lostmekka._3m5gamejam6.entity.attribute

import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.datatypes.extensions.map

class MadnessStorage(
    val maxStorage: Int,
    var storedMadness: Int = 0,
    var collecting: Boolean = false
) : Attribute {
    fun isFull() = storedMadness >= maxStorage
    fun isEmpty() = storedMadness <= 0
}

val AnyGameEntity.madnessStorage get() = tryToFindAttribute(MadnessStorage::class)
