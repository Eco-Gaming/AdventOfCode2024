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
        val positions = ArrayList<Pair<Int, Int>>()
        positions.add(Pair(guardPosition.x, guardPosition.y))
        while (guardPosition.x in matrix.indices && guardPosition.y in matrix[0].indices) {
            val next = arrayOf(guardPosition.x, guardPosition.y)
            when (guardPosition.facing) {
                Facing.NORTH -> next[0]--
                Facing.EAST -> next[1]++
                Facing.SOUTH -> next[0]++
                Facing.WEST -> next[1]--
            }
            try {
                if (!matrix[next[0]][next[1]]) {
                    guardPosition.x = next[0]
                    guardPosition.y = next[1]
                    positions.add(Pair(next[0], next[1]))
                } else {
                    guardPosition.facing = guardPosition.facing.rotateClockwise()
                }
            } catch (e: IndexOutOfBoundsException) {
                break
            }
        }
        return positions.distinct().size.toString()
    }

    override fun solvePartTwo(): String {
        TODO("Not yet implemented")
    }
}