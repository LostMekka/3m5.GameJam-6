package de.lostmekka._3m5gamejam6.world

import de.lostmekka._3m5gamejam6.hammingDistanceTo
import de.lostmekka._3m5gamejam6.neighbours
import org.hexworks.zircon.api.data.impl.Position3D
import kotlin.math.min

private class Node(
    val pos: Position3D,
    val distanceToStart: Int,
    val estimatedDistanceToGo: Int,
    val parent: Node? = null
) {
    val value = distanceToStart + estimatedDistanceToGo
    override fun hashCode() = pos.hashCode()
    override fun equals(other: Any?) = other is Node && other.pos == pos

    val path: MutableList<Position3D>
        get() {
            val path = mutableListOf<Position3D>()
            var currentNode = this as Node?
            while (currentNode?.parent != null) {
                path += currentNode.pos
                currentNode = currentNode.parent
            }
            return path.asReversed()
        }
}

private class SortedNodeStore {
    private val nodesByValue = mutableMapOf<Int, MutableSet<Node>>()
    private var lowestValue: Int = Int.MAX_VALUE

    private fun nodesForValue(value: Int) = nodesByValue.getOrPut(value) { mutableSetOf() }

    fun isEmpty() = nodesByValue.isEmpty()

    operator fun contains(node: Node) = node in nodesForValue(node.value)

    operator fun plusAssign(node: Node) {
        nodesForValue(node.value) += node
        lowestValue = min(lowestValue, node.value)
    }

    fun removeFirst(): Node {
        if (isEmpty()) throw Exception("cannot remove node from empty collection")
        val set = nodesByValue.getValue(lowestValue)
        val node = set.random()
        set -= node
        if (set.isEmpty()) {
            nodesByValue -= lowestValue
            lowestValue = nodesByValue.keys.min() ?: Int.MAX_VALUE
        }
        return node
    }
}

/**
 * Does a breadth first search from the [start] position to the [end] position.
 */
fun World.findPath(start: Position3D, end: Position3D): MutableList<Position3D>? {
    // multiple height levels are not supported for now
    if (start.z != end.z) return null
    // cannot reach unwalkable points
    if (this[end]?.isWalkable != true) return null
    return findPathWithHeuristic(start) { it hammingDistanceTo end }
}

/**
 * Does a breadth first search from the [start] position to the first position that satisfies the [endPredicate].
 */
fun World.findPath(start: Position3D, endPredicate: (Position3D) -> Boolean): MutableList<Position3D>? {
    return findPathWithHeuristic(start) { if (endPredicate(it)) 0 else 1 }
}

/**
 * Does an A* search from the [start] position to any point, where the [heuristic] returns a value of zero or less.
 */
fun World.findPathWithHeuristic(start: Position3D, heuristic: (Position3D) -> Int): MutableList<Position3D>? {
    val toExpand = SortedNodeStore()
    val expanded = mutableSetOf<Node>()
    toExpand += Node(start, 0, heuristic(start))

    while (!toExpand.isEmpty()) {
        val node = toExpand.removeFirst()
        if (node.estimatedDistanceToGo <= 0) return node.path

        expanded += node
        node.pos.neighbours
            .filter { this[it]?.isWalkable == true }
            .map {
                Node(
                    pos = it,
                    distanceToStart = node.distanceToStart + 1,
                    estimatedDistanceToGo = heuristic(it),
                    parent = node
                )
            }
            .filter { it !in expanded && it !in toExpand }
            .forEach { toExpand += it }
    }
    return null
}
