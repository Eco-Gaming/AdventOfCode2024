package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import kotlin.math.abs

fun main() {
    val day02 = Day02()
    day02.solve()
}

class Day02 : Puzzle {

    private val matrix: MutableList<List<Int>> = mutableListOf()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day02.txt")
        val lines = input.lines()

        for (line in lines) {
            matrix.add(line.split(" ").map { it.toInt() })
        }
    }

    override fun solvePartOne(): String {
        var sum = 0
        for (list in matrix) {
            val diffList = mutableListOf<Int>()
            for (i in 0..<list.size - 1) {
                diffList.add(list[i] - list[i + 1])
            }
            if ((diffList.all { it > 0 }) xor (diffList.all { it < 0 })) {
                if (diffList.all { abs(it) in 1..3 }) sum++
            }
        }
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        TODO("Not yet implemented")
    }
}