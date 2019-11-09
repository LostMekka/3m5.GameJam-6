package de.lostmekka._3m5gamejam6.entity.attribute

import de.lostmekka._3m5gamejam6.config.GameConfig
import de.lostmekka._3m5gamejam6.entity.AnyGameEntity
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.createPropertyFrom
import org.hexworks.cobalt.datatypes.extensions.map

class Inventory(
    var torches: Int = GameConfig.playerInitialTorchCount
)

class EntityInventory(initialInventory: Inventory = Inventory()) : Attribute {
    private val inventoryProperty = createPropertyFrom(initialInventory)
    var inventory: Inventory by inventoryProperty.asDelegate()
}

var AnyGameEntity.inventory
    get() = tryToFindAttribute(EntityInventory::class).inventory
    set(value) {
        findAttribute(EntityInventory::class).map {
            it.inventory = value
        }
    }
