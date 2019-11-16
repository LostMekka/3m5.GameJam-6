package de.lostmekka._3m5gamejam6.entity.attribute

import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.datatypes.extensions.map
import org.hexworks.zircon.api.data.impl.Position3D

class LightEmitter(
    val radius: Int,
    var isEnabled: Boolean = true
) : Attribute

val AnyGameEntity.lightEmitter get() = tryToFindAttribute(LightEmitter::class)
