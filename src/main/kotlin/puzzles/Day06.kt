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
        // if turn + step == a previous guard position:
        // distinct list.add(turn.normalNextStep)
        val guardPositions = getGuardPositions(matrix)
        val previousGuardPositions = ArrayList<GuardPosition>()
        val objectPositions = ArrayList<GuardPosition>()
        for ((i, position) in guardPositions.withIndex()) {
            val positionRotated = GuardPosition(position.x, position.y, position.facing.rotateClockwise())
            // if we cross a path that we've been on before
            if (previousGuardPositions.contains(positionRotated)) {
                objectPositions.add(getObjectPos(position))
            } else { // if we can get on our old path by using a new path
                val newMatrix = matrix.map { it.toMutableList() }.toList()
                val newObjectPosition = getObjectPos(position)
                if (newObjectPosition.x == -1 || newObjectPosition.y == -1) continue
                newMatrix[newObjectPosition.x][newObjectPosition.y] = true

                val newPositionList = getGuardPositions(newMatrix, positionRotated)
                // if newPosition results in an endless loop
                // OR if newPositionList has exact matches with our previous path
                // BUT that won't just result in reaching the end
                val overlapPositions = newPositionList.filter { newPos ->
                    previousGuardPositions.contains(newPos)
                }
                // if the index of the overlap position is smaller than the index where we broke out of the list,
                // it means we have found a contender for a new object
                val willResultInLoop = overlapPositions.any { overlapPos ->
                    previousGuardPositions.indexOf(overlapPos) < i
                }
                if (newPositionList.isEmpty() || willResultInLoop) {
                    val objectPos = GuardPosition(position.x, position.y, position.facing)
                    objectPositions.add(getObjectPos(objectPos))
                }
            }
            previousGuardPositions.add(position)
        }
        objectPositions.filter { it.x in matrix.indices && it.y in matrix[0].indices }
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