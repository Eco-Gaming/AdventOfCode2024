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
    private val byteList = mutableListOf<Point>()
    private val limit = 1024

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day18.txt")
        for ((i, line) in input.lines().withIndex()) {
            val x = line.split(",")[0]
            val y = line.split(",")[1]
            if (i < limit) matrix[y.toInt()][x.toInt()] = false
            // x and y are swapped, as per the task:
            // Where X is the distance from the left edge of your memory space
            // and Y is the distance from the top edge of your memory space.
            byteList.add(Point(y.toInt(), x.toInt()))
        }
    }

    // adapted from Day16
    private fun bfs(start: Point, end: Point, inputMatrix: Array<Array<Boolean>> = matrix): Int {
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
                ).filter { it.x in inputMatrix.indices && it.y in inputMatrix[it.x].indices && inputMatrix[it.x][it.y] }
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
        val byteListRemaining = byteList.subList(limit, byteList.size)

        // apply binary search:
        var low = 0
        var high = byteListRemaining.size - 1
        var result: Point? = null
        while (low <= high) {
            val mid = low + ((high - low) / 2)
            val newMatrix = populateMatrix(matrix, byteListRemaining.subList(0, mid + 1))
            if (bfs(Point(0, 0), Point(70, 70), newMatrix) == 0) {
                result = byteListRemaining[mid]
                high = mid - 1
            } else {
                low = mid + 1
            }
        }

        return if (result != null) {
            "${result.y},${result.x}"
        } else {
            "No point found."
        }
    }

    private fun populateMatrix(inputMatrix: Array<Array<Boolean>>, fallingBytes: List<Point>): Array<Array<Boolean>> {
        val newMatrix = inputMatrix.map { it.copyOf() }.toTypedArray()
        for (point in fallingBytes) {
            newMatrix[point.x][point.y] = false
        }
        return newMatrix
    }
}