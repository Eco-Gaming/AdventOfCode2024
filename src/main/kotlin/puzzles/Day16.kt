package me.eco_gaming.puzzles

import me.eco_gaming.Facing
import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import me.eco_gaming.utils.Dijkstra
import me.eco_gaming.utils.graph.Edge
import me.eco_gaming.utils.graph.Graph
import me.eco_gaming.utils.graph.Vertex
import java.awt.Point

fun main() {
    val day16 = Day16()
    day16.solve()
}

class Day16 : Puzzle {

    private val d4 = listOf(
        Pair(Point(0, 1), Facing.EAST),
        Pair(Point(1, 0), Facing.SOUTH),
        Pair(Point(0, -1), Facing.WEST),
        Pair(Point(-1, 0), Facing.NORTH),
    )

    private val matrix = ArrayList<List<Char>>()
    private val start = Point(-1, -1)
    private val end = Point(-1, -1)

    private val graph = Graph<Pair<Point, Facing>>()

    private var bestDistance = Int.MAX_VALUE

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day16.txt")
        for ((i, line) in input.lines().withIndex()) {
            val row = ArrayList<Char>()
            for ((j, char) in line.withIndex()) {
                when (char) {
                    'S' -> {
                        start.x = i
                        start.y = j
                        row.add('.')
                    }
                    'E' -> {
                        end.x = i
                        end.y = j
                        row.add('.')
                    }
                    else -> {
                        row.add(char)
                    }
                }
            }
            matrix.add(row)
        }
        matrixToBulkyGraph(matrix, graph)
    }

    private fun matrixToBulkyGraph(matrix: List<List<Char>>, graph: Graph<Pair<Point, Facing>>) {
        // ensure that the startpoint exists
        graph.addVertex(Vertex(Pair(start, Facing.EAST)))

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == '#') {
                    continue
                }
                // check the right neighbor
                if (matrix[i][j + 1] == '.') {
                    val vertexA = Vertex(Pair(Point(i, j), Facing.EAST))
                    val vertexB = Vertex(Pair(Point(i, j + 1), Facing.WEST))
                    val edge = Edge(vertexA, vertexB, 1.0)
                    graph.addVertex(vertexA)
                    graph.addVertex(vertexB)
                    graph.addEdge(edge)
                }
                // check the bottom neighbor
                if (matrix[i + 1][j] == '.') {
                    val vertexA = Vertex(Pair(Point(i, j), Facing.SOUTH))
                    val vertexB = Vertex(Pair(Point(i + 1, j), Facing.NORTH))
                    val edge = Edge(vertexA, vertexB, 1.0)
                    graph.addVertex(vertexA)
                    graph.addVertex(vertexB)
                    graph.addEdge(edge)
                }
                // connect the 'inner' vertices
                val vertices = listOf(
                    Pair(Point(i, j), Facing.NORTH),
                    Pair(Point(i, j), Facing.EAST),
                    Pair(Point(i, j), Facing.SOUTH),
                    Pair(Point(i, j), Facing.WEST),
                )
                val edges = listOf(
                    Edge(Vertex(vertices[0]), Vertex(vertices[2]), 0.0),
                    Edge(Vertex(vertices[1]), Vertex(vertices[3]), 0.0),
                    Edge(Vertex(vertices[0]), Vertex(vertices[1]), 1000.0),
                    Edge(Vertex(vertices[1]), Vertex(vertices[2]), 1000.0),
                    Edge(Vertex(vertices[2]), Vertex(vertices[3]), 1000.0),
                    Edge(Vertex(vertices[3]), Vertex(vertices[0]), 1000.0),
                )
                // if the one of the vertices is not present,
                // it will be ignored due to logic in the graph class
                edges.forEach { graph.addEdge(it) }
            }
        }
    }

    override fun solvePartOne(): String {
        // find all valid endpoints
        val endpoints = d4.map { Pair(Point(end.x + it.first.x, end.y + it.first.y), it.second) }
            .filter { matrix[it.first.x][it.first.y] == '.' }
            .map { when (it.second) { // remove the offset applied by d4
                Facing.NORTH -> Pair(Point(it.first.x + 1, it.first.y), Facing.NORTH)
                Facing.EAST -> Pair(Point(it.first.x, it.first.y - 1), Facing.EAST)
                Facing.SOUTH -> Pair(Point(it.first.x - 1, it.first.y), Facing.SOUTH)
                Facing.WEST -> Pair(Point(it.first.x, it.first.y + 1), Facing.WEST)
            } }

        val pathList = ArrayList<Pair<Double, List<Vertex<Pair<Point, Facing>>>>>()
        for (endpoint in endpoints) {
            val pair = Dijkstra<Pair<Point, Facing>>().shortestPath(graph, Pair(start, Facing.EAST), endpoint)!!
            pathList.add(pair)
        }

        // find the shortest path
        val shortestPair = pathList.minBy { it.first }
        bestDistance = shortestPair.first.toInt()

        return shortestPair.first.toString()
    }

    override fun solvePartTwo(): String {
        // isn't really compatible with Dijkstra, so use breadth first search instead:
        // List<distance, path>
        val res = bfs(Pair(start, Facing.EAST), end)
        val spots = res.flatMap { it.second }.distinctBy { Pair(it.first.x, it.first.y) }
        return spots.size.toString()
    }

    // [!] this implementation only works, because we have the bestDistance from Dijkstra from part 1 [!]
    private fun bfs(start: Pair<Point, Facing>, end: Point): List<Pair<Int, List<Pair<Point, Facing>>>> {
        val res = ArrayList<Pair<Int, List<Pair<Point, Facing>>>>()

        val queue = ArrayList<Pair<Int, MutableList<Pair<Point, Facing>>>>()
        val seen = ArrayList<Pair<Point, Facing>>()

        queue.add(Pair(0, mutableListOf(start)))

        while (queue.isNotEmpty()) {
            val (distance, path) = queue.removeFirst()
            val last = path.last()
            if (distance > bestDistance) continue

            if (last.first == end) {
                res.add(Pair(distance, path))
                continue
            }
            seen.add(last)

            // check forward
            val nextForward = when (last.second) {
                Facing.NORTH -> Pair(Point(last.first.x - 1, last.first.y), Facing.NORTH)
                Facing.EAST -> Pair(Point(last.first.x, last.first.y + 1), Facing.EAST)
                Facing.SOUTH -> Pair(Point(last.first.x + 1, last.first.y), Facing.SOUTH)
                Facing.WEST -> Pair(Point(last.first.x, last.first.y - 1), Facing.WEST)
            }
            if (matrix[nextForward.first.x][nextForward.first.y] == '.' && nextForward !in seen) {
                val newPath = path.toMutableList()
                newPath.add(nextForward)
                queue.add(Pair(distance + 1, newPath))
            }

            // check left
            val nextLeft = when (last.second) {
                Facing.NORTH -> Pair(Point(last.first.x, last.first.y - 1), Facing.WEST)
                Facing.EAST -> Pair(Point(last.first.x - 1, last.first.y), Facing.NORTH)
                Facing.SOUTH -> Pair(Point(last.first.x, last.first.y + 1), Facing.EAST)
                Facing.WEST -> Pair(Point(last.first.x + 1, last.first.y), Facing.SOUTH)
            }
            if (matrix[nextLeft.first.x][nextLeft.first.y] == '.' && nextLeft !in seen) {
                val newPath = path.toMutableList()
                newPath.add(nextLeft)
                queue.add(Pair(distance + 1001, newPath))
            }

            // check right
            val nextRight = when (last.second) {
                Facing.NORTH -> Pair(Point(last.first.x, last.first.y + 1), Facing.EAST)
                Facing.EAST -> Pair(Point(last.first.x + 1, last.first.y), Facing.SOUTH)
                Facing.SOUTH -> Pair(Point(last.first.x, last.first.y - 1), Facing.WEST)
                Facing.WEST -> Pair(Point(last.first.x - 1, last.first.y), Facing.NORTH)
            }
            if (matrix[nextRight.first.x][nextRight.first.y] == '.' && nextRight !in seen) {
                val newPath = path.toMutableList()
                newPath.add(nextRight)
                queue.add(Pair(distance + 1001, newPath))
            }
        }
        return res
    }
}