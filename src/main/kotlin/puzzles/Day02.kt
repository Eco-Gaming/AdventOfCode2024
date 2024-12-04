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
            val diffList = getDiffList(list)
            if (checkDiffList(diffList)) sum++
        }
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        var sum = 0
        for (list in matrix) {
            val diffList = getDiffList(list)
            if (checkDiffList(diffList)) {
                sum++
            } else {
                for (i in list.indices) {
                    val listModified = list.toMutableList()
                    listModified.removeAt(i)
                    if (checkDiffList(getDiffList(listModified))) {
                        sum++
                        break;
                    }
                }
            }

        }
        return sum.toString()
    }

    private fun getDiffList(line: List<Int>) : List<Int> {
        val diffList = mutableListOf<Int>()
        for (i in 0..<line.size - 1) {
            diffList.add(line[i] - line[i + 1])
        }
        return diffList
    }

    private fun checkDiffList(diffList: List<Int>): Boolean {
        if ((diffList.all { it > 0 }) xor (diffList.all { it < 0 })) {
            if (diffList.all { abs(it) in 1..3 }) return true
        }
        return false
    }
}