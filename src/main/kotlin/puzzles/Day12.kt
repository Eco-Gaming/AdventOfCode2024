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

fun Point.getAllNeighbors(): List<Point> {
    return listOf(
        Point(x, y + 1),
        Point(x, y - 1),
        Point(x + 1, y),
        Point(x - 1, y),
        Point(x + 1, y + 1),
        Point(x + 1, y - 1),
        Point(x - 1, y + 1),
        Point(x - 1, y - 1),
    )
}

fun Point.getValue(matrix: List<List<Char>>): Char {
    if (x in matrix.indices && y in matrix[x].indices) return matrix[x][y]
    return 0.toChar()
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
        val entries = calculateRegions()
        val price = calculateFencePricePartTwo(entries)
        return price.toString()
    }

    private fun calculateFencePricePartTwo(entries: List<ListEntry>): Long {
        var sum = 0L
        for (entry in entries) {
            val corners = countCorners(entry)
            sum += entry.area * corners
        }
        return sum
    }

    // A corner is where a 2x2 grid contains one/three in-shape points,
    // or two diagonally-opposite cells.
    // idea from: https://programming.dev/post/22749375/13849136
    private fun countCorners(entry: ListEntry): Int {
        var corners = 0
        val borders = mutableListOf<Point>()
        entry.points.forEach { point -> // add all points with neighbors + diagonal neighbors
            borders.add(point)
            borders.addAll(point.getAllNeighbors())
        }
        for (cell in borders.distinct()) {
            // look at a 2x2 grid
            val cells = listOf(
                Point(cell.x, cell.y),
                Point(cell.x, cell.y + 1),
                Point(cell.x + 1, cell.y),
                Point(cell.x + 1, cell.y + 1),
            ).map { entry.points.contains(it) }
            if (cells.count { it } % 2 == 1) { // 1 or 3 correct points
                corners++
            } else if (cells == listOf(true, false, false, true) || cells == listOf(false, true, true, false)) { // 2 diagonally opposite points
                corners += 2
            }
        }
        return corners
    }
}