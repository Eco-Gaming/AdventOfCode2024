package me.eco_gaming.utils

import me.eco_gaming.utils.graph.Graph
import me.eco_gaming.utils.graph.Vertex

class Dijkstra<E> {

    // Pair<distance, path>
    fun shortestPath(graph: Graph<E>, start: E, end: E): Pair<Double, List<Vertex<E>>>? {
        val paths = dijkstra(graph, start)
        val endVertex = graph.getVertex(end) ?: return null
        val distance = paths[endVertex]?.first ?: return null

        val pathWrongOrder = pathHelper(paths, endVertex, ArrayList())
        return Pair(distance, pathWrongOrder.reversed())
    }

    private fun pathHelper(paths: Map<Vertex<E>, Pair<Double, Vertex<E>?>>, end: Vertex<E>, path: MutableList<Vertex<E>>): List<Vertex<E>> {
        path.add(end)
        val newEnd = paths[end]?.second
        return if (end == newEnd) {
            path
        } else {
            pathHelper(paths, newEnd!!, path)
        }
    }

    fun dijkstra(graph: Graph<E>, start: E): Map<Vertex<E>, Pair<Double, Vertex<E>?>> {
        val max = Double.MAX_VALUE
        val vertices = graph.getVertices()
        val unoptimized = ArrayList<Vertex<E>>()
        val distances = HashMap<Vertex<E>, Pair<Double, Vertex<E>?>>()

        val source = graph.getVertex(start) ?: return distances

        for (vertex in vertices) {
            distances[vertex] = Pair(max, null)
            distances[source] = Pair(0.0, source)
            unoptimized.add(vertex)
        }

        while (unoptimized.isNotEmpty()) {
            val u = getClosestNode(unoptimized, distances)
            unoptimized.remove(u)

            val neighbors = graph.getNeighbors(u)
            for (neighbor in neighbors) {
                if (unoptimized.contains(neighbor)) {
                    val alt = (distances[u]?.first ?: 0.0) + (graph.getEdge(u, neighbor)?.weight!!)
                    if (alt < (distances[neighbor]?.first ?: max)) {
                        distances[neighbor] = Pair(alt, u)
                    }
                }
            }
        }

        return distances
    }

    private fun getClosestNode(unoptimized: List<Vertex<E>>, distances: Map<Vertex<E>, Pair<Double, Vertex<E>?>>): Vertex<E> {
        var closest = unoptimized[0]
        var distance = distances[closest]?.first ?: Double.MAX_VALUE

        for (vertex in unoptimized) {
            val distance2 = distances[vertex]?.first ?: Double.MAX_VALUE
            if (distance2 < distance) {
                closest = vertex
                distance = distance2
            }
        }

        return closest
    }
}