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
        return getGuardPositions(matrix).distinctBy { Pair(it.x, it.y) }.size.toString()
    }

    override fun solvePartTwo(): String {
        // (mostly) brute force for the win
        val guardPositions = getGuardPositions(matrix)
        val objectPositions = ArrayList<GuardPosition>()
        guardPositions.parallelStream().forEach { position ->
            val objectPosition = getObjectPos(position)
            val newMatrix = matrix.map { it.toMutableList() }.toList()
            if (objectPosition.x == -1 || objectPosition.y == -1) return@forEach
            newMatrix[objectPosition.x][objectPosition.y] = true
            if (getGuardPositions(newMatrix).isEmpty()) synchronized(objectPositions) {
                objectPositions.add(objectPosition)
            }
        }
        return objectPositions.distinctBy { Pair(it.x, it.y) }.size.toString()
    }

    private fun getGuardPositions(pMatrix: List<List<Boolean>>, startPosition: GuardPosition = guardPosition): List<GuardPosition> {
        val positions = ArrayList<GuardPosition>()
        val newGuardPosition = GuardPosition(startPosition.x, startPosition.y, startPosition.facing)
        while (newGuardPosition.x in pMatrix.indices && newGuardPosition.y in pMatrix[0].indices) {
            // return an empty list if we got into an endless loop
            if (positions.contains(newGuardPosition)) return listOf()
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
}