package me.eco_gaming.puzzles

import me.eco_gaming.*
import java.awt.Point

fun main() {
    val day15 = Day15()
    day15.solve()
}

class Day15 : Puzzle {

    private val matrix = ArrayList<ArrayList<Char>>()
    private val robot = Day15Robot(-1, -1)
    private val movements = ArrayList<Facing>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day15.txt")
        var modeSwitch = false
        for (line in input.lines()) {
            if (line.isBlank()) {
                modeSwitch = true
                continue
            }
            if (!modeSwitch) {
                val lineList = ArrayList<Char>()
                for (char in line) {
                    if (char == '@') {
                        robot.x = matrix.size
                        robot.y = lineList.size
                        lineList.add('.')
                    } else {
                        lineList.add(char)
                    }
                }
                matrix.add(lineList)
            } else {
                for (char in line) {
                    when (char) {
                        '^' -> movements.add(Facing.NORTH)
                        '>' -> movements.add(Facing.EAST)
                        'v' -> movements.add(Facing.SOUTH)
                        '<' -> movements.add(Facing.WEST)
                    }
                }
            }
        }
    }

    override fun solvePartOne(): String {
        val newMatrix = matrix.map { it.toMutableList() }.toList()
        val newRobot = robot.copy()
        for (movement in movements) {
            newRobot.step(newMatrix, movement)
        }
        val sum = calculateGps(newMatrix)
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        val (newMatrix, boxList) = translateMatrix(matrix)
        val emptyMatrix = newMatrix.map { list -> list.map {
                if (it == '[' || it == ']') {
                    '.'
                } else {
                    it
                }
            }
        }
        val newRobot = robot.copy()
        newRobot.y *= 2
        for (movement in movements) {
            newRobot.stepPartTwo(emptyMatrix, boxList, movement)
        }
        val newNewMatrix = populateMatrix(emptyMatrix, boxList)
        val sum = calculateGps(newNewMatrix, '[')
        return sum.toString()
    }

    private fun calculateGps(matrix: List<List<Char>>, boxChar: Char = 'O'): Long {
        var sum = 0L
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == boxChar) {
                    sum += 100*i + j
                }
            }
        }
        return sum
    }

    private fun translateMatrix(matrix: List<List<Char>>): Pair<List<MutableList<Char>>, MutableList<Box>> {
        val newMatrix = ArrayList<ArrayList<Char>>()
        val boxList = ArrayList<Box>()
        for (i in matrix.indices) {
            val list = ArrayList<Char>()
            for (j in matrix[i].indices) {
                when (matrix[i][j]) {
                    '#' -> list.addAll(listOf('#', '#'))
                    '.' -> list.addAll(listOf('.', '.'))
                    'O' -> {
                        list.addAll(listOf('[', ']'))
                        boxList.add(Box(Point(i, j*2), Point(i, j*2 + 1)))
                    }
                }
            }
            newMatrix.add(list)
        }
        return Pair(newMatrix, boxList)
    }

    private fun populateMatrix(emptyMatrix: List<List<Char>>, boxList: List<Box>): List<List<Char>> {
        val newMatrix = emptyMatrix.map { it.toMutableList() }.toList()
        for (box in boxList) {
            newMatrix[box.left.x][box.left.y] = '['
            newMatrix[box.right.x][box.right.y] = ']'
        }
        return newMatrix
    }
}