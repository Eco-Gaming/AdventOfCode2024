package me.eco_gaming.utils.graph

/**
 * Represents a weighted, but non-directional graph.
 */
class Graph<E> {

    private val vertices = ArrayList<Vertex<E>>()
    private val edges = ArrayList<Edge<E>>()

    fun getVertices(): List<Vertex<E>> {
        return vertices
    }

    fun getEdges(): List<Edge<E>> {
        return edges
    }

    fun getVertex(content: E): Vertex<E>? {
        return vertices.firstOrNull { it.content == content }
    }

    fun addVertex(vertex: Vertex<E>) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex)
        }
    }

    /**
     * Adds an edge to the graph, if both vertices are present, aren't equal and the edge is not already present.
     */
    fun addEdge(edge: Edge<E>) {
        if (!edges.contains(edge) && edge.getVertices().first != edge.getVertices().second &&
            vertices.contains(edge.getVertices().first) && vertices.contains(edge.getVertices().second)) {
            edges.add(edge)
        }
    }

    fun removeVertex(vertex: Vertex<E>) {
        TODO("Not yet implemented")
    }

    fun removeEdge(edge: Edge<E>) {
        TODO("Not yet implemented")
    }

    fun setAllVertexMarks(mark: Boolean) {
        for (vertex in vertices) {
            vertex.mark = mark
        }
    }

    fun setAllEdgeMarks(mark: Boolean) {
        for (edge in edges) {
            edge.mark = mark
        }
    }

    fun allVerticesMarked(): Boolean {
        return vertices.all { it.mark }
    }

    fun allEdgesMarked(): Boolean {
        return edges.all { it.mark }
    }

    fun getNeighbors(vertex: Vertex<E>): List<Vertex<E>> {
        val neighbors = ArrayList<Vertex<E>>()
        for (edge in edges) {
            if (edge.getVertices().first == vertex) {
                neighbors.add(edge.getVertices().second)
            } else if (edge.getVertices().second == vertex) {
                neighbors.add(edge.getVertices().first)
            }
        }
        return neighbors
    }

    fun getEdges(vertex: Vertex<E>): List<Edge<E>> {
        TODO("Not yet implemented")
    }

    fun getEdge(vertexA: Vertex<E>, vertexB: Vertex<E>): Edge<E>? {
        return edges.firstOrNull { it.getVertices() == Pair(vertexA, vertexB) || it.getVertices() == Pair(vertexB, vertexA) }
    }

    fun isEmpty(): Boolean {
        return vertices.isEmpty()
    }
}