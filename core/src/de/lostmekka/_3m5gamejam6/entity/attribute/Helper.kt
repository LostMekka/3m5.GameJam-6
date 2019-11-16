package de.lostmekka._3m5gamejam6.entity.attribute

import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.datatypes.extensions.orElseThrow
import kotlin.reflect.KClass

fun <T : Attribute> AnyGameEntity.tryToFindAttribute(klass: KClass<T>): T =
    findAttribute(klass).orElseThrow {
        NoSuchElementException("Entity '$this' has no property with type '${klass.simpleName}'.")
    }

inline fun <reified T : Attribute> AnyGameEntity.findAttributeOrNull(): T? =
    findAttribute(T::class).let { if (it.isPresent) it.get() else null }

inline fun <reified T : Attribute> AnyGameEntity.hasAttribute(): Boolean =
    findAttribute(T::class).isPresent
