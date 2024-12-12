package me.eco_gaming.puzzles

import me.eco_gaming.ListEntry
import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import java.awt.Point

fun main() {
    val day12 = Day12()
    day12.solve()
}

fun Point.inBounds(matrix: List<List<Char>>): Boolean {
    return x in matrix.indices && y in matrix[x].indices
}

fun Point.getNeighbors(matrix: List<List<Char>>): List<Point> {
    val list = ArrayList<Point>()
    if (x > 0) list.add(Point(x - 1, y))
    if (y > 0) list.add(Point(x, y - 1))
    if (x < matrix.size - 1) list.add(Point(x + 1, y))
    if (y < matrix[x].size - 1) list.add(Point(x, y + 1))
    return list
}

fun Point.getValue(matrix: List<List<Char>>): Char {
    return matrix[x][y]
}

fun Point.getForeignEdgeCount(matrix: List<List<Char>>): Int {
    val list = getNeighbors(matrix)
    val output = ArrayList<Point>()
    list.forEach { if (it.getValue(matrix) != getValue(matrix)) output.add(it) }
    return output.size + (4 - list.size) // in case the edges are borders of our matrix
}

class Day12 : Puzzle {

    private val matrix = ArrayList<ArrayList<Char>>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day12.txt")
        for (line in input.lines()) {
            val list = ArrayList<Char>()
            for (char in line) {
                list.add(char)
            }
            matrix.add(list)
        }
    }

    private fun calculateRegions(): ArrayList<ListEntry> {
        val output = ArrayList<ListEntry>()

        // calculate regions & perimeters
        for ((i, row) in matrix.withIndex()) {
            for ((j, cell) in row.withIndex()) {
                val point = Point(i, j)

                if (output.any { it.char == cell }) {
                    val entries = output.filter { it.char == cell }
                    var found = false
                    for (entry in entries) {
                        if (arePointsConnected(entry.points[0], point)) {
                            entry.points.add(point)
                            entry.area++
                            found = true
                            break
                        }
                    }
                    if (!found) {
                        output.add(ListEntry(cell, 1, 0, mutableListOf(point)))
                    }
                } else {
                    output.add(ListEntry(cell, 1, 0, mutableListOf(point)))
                }

                output.filter { it.points.contains(point) }[0].perimeter += point.getForeignEdgeCount(matrix)
            }
        }
        return output
    }

    private fun calculateFencePrice(list: ArrayList<ListEntry>): Long {
        var sum = 0L
        for (entry in list) {
            sum += entry.area * entry.perimeter
        }
        return sum
    }

    private fun arePointsConnected(point1: Point, point2: Point, inputMatrix: List<List<Char>> = matrix): Boolean {
        val visited = Array(inputMatrix.size) { BooleanArray(inputMatrix[0].size) }
        return depthFirstSearch(point1.x, point1.y, point2.x, point2.y, matrix[point1.x][point1.y], visited)
    }

    private fun depthFirstSearch(startX: Int, startY: Int, endX: Int, endY: Int, char: Char, visitedList: Array<BooleanArray>, inputMatrix: List<List<Char>> = matrix): Boolean {
        if (startX == endX && startY == endY) return true

        visitedList[startX][startY] = true
        val directions = arrayOf(
            Pair(-1, 0), // up
            Pair(1, 0),  // down
            Pair(0, -1), // left
            Pair(0, 1)   // right
        )
        var condition = false
        for (direction in directions) {
            val point = Point(startX + direction.first, startY + direction.second)
            if (point.inBounds(inputMatrix) && point.getValue(inputMatrix) == char && !visitedList[point.x][point.y]) {
                condition = condition || depthFirstSearch(point.x, point.y, endX, endY, char, visitedList, inputMatrix)
            }
        }
        return condition
    }

    override fun solvePartOne(): String {
        val entries = calculateRegions()
        val price = calculateFencePrice(entries)
        return price.toString()
    }

    override fun solvePartTwo(): String {
        return ""
    }
}