package me.eco_gaming.puzzles

import me.eco_gaming.Facing
import me.eco_gaming.GuardPosition
import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day06 = Day06()
    day06.solve()
}

class Day06 : Puzzle {

    private val matrix: MutableList<MutableList<Boolean>> = mutableListOf()
    private val guardPosition = GuardPosition(-1, -1, Facing.NORTH)

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day06.txt")
        for ((index1, line) in input.lines().withIndex()) {
            val rowList = ArrayList<Boolean>()
            for ((index2, char) in line.withIndex()) {
                when (char) {
                    '#' -> rowList.add(true)
                    '.' -> rowList.add(false)
                    else -> {
                        rowList.add(false)
                        guardPosition.x = index1
                        guardPosition.y = index2
                        when (char) {
                            '^' -> guardPosition.facing = Facing.NORTH
                            '>' -> guardPosition.facing = Facing.EAST
                            '<' -> guardPosition.facing = Facing.WEST
                            'v' -> guardPosition.facing = Facing.SOUTH
                            else -> {
                                guardPosition.x = -1
                                guardPosition.y = -1
                            }
                        }
                    }
                }
            }
            matrix.add(rowList)
        }
    }

    override fun solvePartOne(): String {
        return getGuardPositions(matrix).distinct().size.toString()
    }

    override fun solvePartTwo(): String {
        return partTwoBruteForce()

        // NONE OF THIS WORKS, USE BRUTEFORCE INSTEAD!

        // if turn + step == a previous guard position:
        // distinct list.add(turn.normalNextStep)
        val guardPositions = getGuardPositions(matrix)
        val previousGuardPositions = ArrayList<GuardPosition>()
        val objectPositions = ArrayList<GuardPosition>()
        for (position in guardPositions) {
            val newPosition = GuardPosition(position.x, position.y, position.facing.rotateClockwise())
            // if we cross a path that we've been on before
            if (previousGuardPositions.any { it.fullEquals(newPosition) }) {
                objectPositions.add(getObjectPos(position))
            } else { // if we can get on our old path by using a new path
                val newNewPosition = GuardPosition(newPosition.x, newPosition.y, newPosition.facing)
                // if we rotate, and can then go straight till we reach a previous path, we can take the base point + rotate CCW + step
                while (newNewPosition.facing == newPosition.facing && newNewPosition.x >= 0) {
                    newNewPosition.step(matrix)
                }
                if (previousGuardPositions.any { it.fullEquals(newNewPosition) }) {
                    val objectPos = GuardPosition(position.x, position.y, position.facing)
                    objectPositions.add(getObjectPos(objectPos))
                }
            }
            previousGuardPositions.add(position)
        }
        return objectPositions.distinct().size.toString()
    }

    private fun getGuardPositions(pMatrix: List<List<Boolean>>): List<GuardPosition> {
        val positions = ArrayList<GuardPosition>()
        val newGuardPosition = GuardPosition(guardPosition.x, guardPosition.y, guardPosition.facing)
        while (newGuardPosition.x in pMatrix.indices && newGuardPosition.y in pMatrix[0].indices) {
            // return an empty list if we got into an endless loop
            if (positions.any { it.fullEquals(newGuardPosition) }) return listOf()
            positions.add(GuardPosition(newGuardPosition.x, newGuardPosition.y, newGuardPosition.facing))
            newGuardPosition.step(pMatrix)
        }
        return positions
    }

    private fun getObjectPos(position: GuardPosition): GuardPosition {
        val objectPos = GuardPosition(position.x, position.y, position.facing)
        objectPos.step(matrix)
        return objectPos
    }

    private fun partTwoBruteForce(): String {
        var sum = 0
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (!matrix[i][j]) {
                    println("Iteration " + (i * matrix[0].size + j) + "/" + (matrix.size * matrix[0].size))
                    val matrixCopy = matrix.map { it.toMutableList() }.toList()
                    matrixCopy[i][j] = true
                    val positions = getGuardPositions(matrixCopy)
                    if (positions.isEmpty()) sum++
                }
            }
        }
        return sum.toString()
    }
}