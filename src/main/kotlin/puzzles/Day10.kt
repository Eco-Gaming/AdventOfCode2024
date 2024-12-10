package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import java.awt.Point

fun main() {
    val day10 = Day10()
    day10.solve()
}

fun Point.getNeighbors(matrix: List<List<Int>>): List<Point> {
    val list = ArrayList<Point>()
    if (x > 0) list.add(Point(x - 1, y))
    if (y > 0) list.add(Point(x, y - 1))
    if (x < matrix.size - 1) list.add(Point(x + 1, y))
    if (y < matrix[x].size - 1) list.add(Point(x, y + 1))
    return list
}

fun Point.getValue(matrix: List<List<Int>>): Int {
    return matrix[x][y]
}

class Day10 : Puzzle {

    private val matrix: MutableList<List<Int>> = mutableListOf()
    private val startPoints = ArrayList<Point>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day10.txt")
        val lines = input.lines()

        for (line in lines) {
            matrix.add(line.map { it.digitToInt() })
        }

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                when (matrix[i][j]) {
                    0 -> startPoints.add(Point(i, j))
                }
            }
        }
    }

    private fun countPaths(startPoint: Pair<Int, Point>, endPoints: MutableList<Point>, pointMatrix: List<List<Int>> = matrix) {
        val neighbors = startPoint.second.getNeighbors(pointMatrix)
        val contenders = ArrayList<Point>()
        for (neighbor in neighbors) {
            if (neighbor.getValue(pointMatrix) == startPoint.first + 1) {
                if (startPoint.first == 8) {
                    endPoints.add(neighbor)
                } else {
                    contenders.add(neighbor)
                }
            }
        }
        contenders.forEach { countPaths(Pair(startPoint.first + 1, it), endPoints) }
    }

    override fun solvePartOne(): String {
        var sum = 0
        for (point in startPoints) {
            val endPoints = ArrayList<Point>()
            countPaths(Pair(0, point), endPoints)
            sum += endPoints.distinct().size
        }
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        var sum = 0
        for (point in startPoints) {
            val endPoints = ArrayList<Point>()
            countPaths(Pair(0, point), endPoints)
            sum += endPoints.size
        }
        return sum.toString()
    }
}