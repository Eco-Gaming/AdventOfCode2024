package me.eco_gaming.utils.graph

import java.util.*

/**
 * Represents an edge in a graph.
 */
class Edge<E>(private val vertexA: Vertex<E>, private val vertexB: Vertex<E>, var weight: Double) {

    var mark = false

    fun getVertices(): Pair<Vertex<E>, Vertex<E>> {
        return Pair(vertexA, vertexB)
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is Edge<*> -> vertexA == other.vertexA && vertexB == other.vertexB && weight == other.weight
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(vertexA, vertexB, weight)
    }
}