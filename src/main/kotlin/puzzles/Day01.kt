package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import kotlin.math.abs

fun main() {
    val day01 = Day01()
    day01.solve()
}

class Day01 : Puzzle {

    private val list1 = mutableListOf<Int>()
    private val list2 = mutableListOf<Int>()

    override fun readFile() {
        val filePath = "src/main/resources/day01.txt"
        val listString = readInputFromFile(filePath)
        for (s in listString.lines()) {
            val lineArr = s.split("\\s+".toRegex())
            list1.add(lineArr[0].toInt())
            list2.add(lineArr[1].toInt())
        }
    }

    override fun solvePartOne(): String {
        list1.sort()
        list2.sort()

        if (list1.size != list2.size) throw RuntimeException("lists are not of same length")

        var sum = 0
        for (i in list1.indices) {
            sum += abs(list1[i] - list2[i])
        }
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        var sum = 0
        for (i in list1.indices) {
            sum += list1[i] * list2.count { it == list1[i] }
        }
        return sum.toString()
    }
}