package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    val day11 = Day11()
    day11.solve()
}

class Day11 : Puzzle {

    private val inputList = ArrayList<Long>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day11.txt")
        input.split(" ").forEach { inputList.add(it.toLong()) }
    }

    override fun solvePartOne(): String {
        return getList(25).size.toString()
    }

    override fun solvePartTwo(): String {
        // return getList(75).size.toString()
        return ""
    }

    private fun getList(iterations: Int): List<Long> {
        val input = inputList.toMutableList()
        val output = ArrayList<Long>()
        for (j in 0..<iterations) {
            for (num in input) {
                val digits = countDigits(num)
                if (num == 0L) {
                    output.add(1)
                } else if (digits % 2 == 0L) {
                    val left = num / 10.0.pow(digits / 2.0).toInt()
                    val right = num % 10.0.pow(digits / 2.0).toInt()
                    output.add(left)
                    output.add(right)
                } else {
                    output.add(num * 2024)
                }
            }
            input.clear()
            input.addAll(output)
            output.clear()
        }
        return input
    }

    private fun countDigits(input: Long): Long {

        return floor( log10( input.toDouble() ) ).toLong() + 1;

        var n = 1L
        var num = input
        if ( num >= 100000000 ) { n += 8; num /= 100000000; }
        if ( num >= 10000     ) { n += 4; num /= 10000; }
        if ( num >= 100       ) { n += 2; num /= 100; }
        if ( num >= 10        ) { n += 1; }
        return n
    }
}