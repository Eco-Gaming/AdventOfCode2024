package me.eco_gaming.utils.graph

import java.util.Objects

/**
 * Represents a vertex (node) in a graph.
 */
class Vertex<E>(val content: E) {
    var mark = false

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is Vertex<*> -> content == other.content
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(content)
    }
}