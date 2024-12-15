package me.eco_gaming.puzzles

import me.eco_gaming.Day15Robot
import me.eco_gaming.Facing
import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

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
        for (movement in movements) {
            robot.step(newMatrix, movement)
        }
        val sum = calculateGps(newMatrix)
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        TODO("Not yet implemented")
    }

    private fun calculateGps(matrix: List<List<Char>>): Long {
        var sum = 0L
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == 'O') {
                    sum += 100*i + j
                }
            }
        }
        return sum
    }
}