package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import java.awt.Point

fun main() {
    val day18 = Day18()
    day18.solve()
}

class Day18 : Puzzle {

    // y by x boolean array, currently all spots are safe
    private val matrix = Array(71) { Array(71) { true } }
    private val limit = 1024

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day18.txt")
        for ((i, line) in input.lines().withIndex()) {
            if (i >= limit) break
            val x = line.split(",")[0]
            val y = line.split(",")[1]
            matrix[y.toInt()][x.toInt()] = false
            // x and y are swapped, as per the task:
            // Where X is the distance from the left edge of your memory space
            // and Y is the distance from the top edge of your memory space.
        }
    }

    // adapted from Day16
    private fun bfs(start: Point, end: Point): Int {
        val queue = ArrayDeque<Pair<Int, Point>>()
        val seen = HashSet<Point>()

        queue.add(Pair(0, start))
        while (queue.isNotEmpty()) {
            val (dist, current) = queue.removeFirst()
            if (current !in seen) {
                seen.add(current)
                if (current == end) {
                    return dist
                }

                val x = current.x
                val y = current.y
                listOf(
                    Point(x + 1, y),
                    Point(x, y + 1),
                    Point(x - 1, y),
                    Point(x, y - 1),
                ).filter { it.x in matrix.indices && it.y in matrix[it.x].indices && matrix[it.x][it.y] }
                    .forEach { queue.add(Pair(dist + 1, it)) }
            }
        }
        return 0
    }

    override fun solvePartOne(): String {
        val distance = bfs(Point(0, 0), Point(70, 70))
        return distance.toString()
    }

    override fun solvePartTwo(): String {
        TODO("Not yet implemented")
    }
}