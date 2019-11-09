package de.lostmekka._3m5gamejam6.entity.attribute

import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.createPropertyFrom
import org.hexworks.cobalt.datatypes.extensions.map

class EntityHealth(initialHealth: Int = 100) : Attribute {
    private val healthProperty = createPropertyFrom(initialHealth)
    var health: Int by healthProperty.asDelegate()
}

var AnyGameEntity.health
    get() = tryToFindAttribute(EntityHealth::class).health
    set(value) {
        findAttribute(EntityHealth::class).map {
            it.health = value
        }
    }
