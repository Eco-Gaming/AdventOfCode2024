package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import java.awt.Point
import kotlin.math.abs

fun main() {
    val day20 = Day20()
    day20.solve()
}

class Day20 : Puzzle {

    private val timeSave = 100 // read from task

    private val matrix = ArrayList<List<Char>>()
    private val start = Point(-1, -1)
    private val end = Point(-1, -1)

    private val d2x = listOf(Point(1, 0), Point(-1, 0))
    private val d2y = listOf(Point(0, 1), Point(0, -1))

    private val bfsCache = HashMap<Pair<Point, Point>, Int>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day20.txt")
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
    }

    override fun solvePartOne(): String {
        val distance = bfs(start, end)
        val maxDistance = distance - timeSave

        val removableNodes = getRemovableNodes()
        val result = removableNodes.parallelStream()
            .map { bfs(start, it) + bfs(it, end) }
            .filter { it <= maxDistance }
            .count()

        return result.toString()
    }

    private fun getRemovableNodes(): List<Point> {
        val removableNodes = ArrayList<Point>()
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == '#') {
                    val checkX = d2x.map { Point(i + it.x, j + it.y) }
                        .filter { it.x in matrix.indices && it.y in matrix[it.x].indices }
                        .count { matrix[it.x][it.y] == '.' } == 2
                    val checkY = d2y.map { Point(i + it.x, j + it.y) }
                        .filter { it.x in matrix.indices && it.y in matrix[it.x].indices }
                        .count { matrix[it.x][it.y] == '.' } == 2
                    if (checkX || checkY) {
                        removableNodes.add(Point(i, j))
                    }
                }
            }
        }
        return removableNodes
    }

    // adapted from Day18
    // start and end are allowed to be '#'
    private fun bfs(start: Point, end: Point, inputMatrix: List<List<Char>> = matrix): Int {
        if (Pair(start, end) in bfsCache) return bfsCache[Pair(start, end)]!!

        val queue = ArrayDeque<Pair<Int, Point>>()
        val seen = HashSet<Point>()

        queue.add(Pair(0, start))
        while (queue.isNotEmpty()) {
            val (dist, current) = queue.removeFirst()
            if (current !in seen) {
                seen.add(current)
                if (current == end) {
                    bfsCache[Pair(start, end)] = dist
                    return dist
                }

                val x = current.x
                val y = current.y
                val contenders = listOf(
                    Point(x + 1, y),
                    Point(x, y + 1),
                    Point(x - 1, y),
                    Point(x, y - 1),
                ).filter { it.x in inputMatrix.indices && it.y in inputMatrix[it.x].indices }

                if (contenders.contains(end)) {
                    bfsCache[Pair(start, end)] = dist + 1
                    return dist + 1
                }

                contenders.filter { inputMatrix[it.x][it.y] == '.' }
                    .forEach { queue.add(Pair(dist + 1, it)) }
            }
        }
        return 0
    }

    override fun solvePartTwo(): String {
        val distance = bfs(start, end)
        val maxDistance = distance - timeSave
        val path = getPathList(start, end)

        // pretty much brute force, but runs in under 5 mins so it's fine :+1:
        val removableNodeList = getRemovableNodeList(path)
        val result = removableNodeList.parallelStream()
            .map { bfs(start, it.first) + bfs(it.second, end) + it.third }
            .filter { it <= maxDistance }
            .count()

        return result.toString()
    }

    // this function works, because there is exactly 1 possible path
    private fun getPathList(start: Point, end: Point): List<Point> {
        val path = ArrayList<Point>()
        val seen = HashSet<Point>()

        var current = start.clone() as Point
        while (current != end) {
            path.add(current)
            seen.add(current)
            val next = listOf(
                Point(current.x + 1, current.y),
                Point(current.x, current.y + 1),
                Point(current.x - 1, current.y),
                Point(current.x, current.y - 1),
            ).filter { it.x in matrix.indices && it.y in matrix[it.x].indices }
                .filter { matrix[it.x][it.y] == '.' }
                .first { it !in seen }
            current = next
        }

        return path
    }

    // Pair<start, end, distance>
    private fun getRemovableNodeList(path: List<Point>): List<Triple<Point, Point, Int>> {
        val removableNodeList = ArrayList<Triple<Point, Point, Int>>()
        val neighborList = getAllPossibleNeighbors(20)

        for (point in path) {
            neighborList.map { Point(point.x + it.x, point.y + it.y) }
                .filter { it.x in matrix.indices && it.y in matrix[it.x].indices }
                .filter { matrix[it.x][it.y] == '.' }
                .forEach {
                    val start = Point(point.x, point.y)
                    val end = Point(it.x, it.y)
                    val distance = bfs(start, end)
                    val optimalDistance = abs(start.x - end.x) + abs(start.y - end.y)
                    // check if the skip is capable of saving time
                    if (distance != optimalDistance) {
                        removableNodeList.add(Triple(start, end, optimalDistance))
                    }
                }
        }
        return removableNodeList
    }

    // diamond pattern with the specified radius
    private fun getAllPossibleNeighbors(radius: Int): List<Point> {
        val neighbors = ArrayList<Point>()
        for (i in -radius..radius) {
            for (j in -radius..radius) {
                if ((abs(i) + abs(j)) <= radius) {
                    neighbors.add(Point(i, j))
                }
            }
        }
        return neighbors
    }
}