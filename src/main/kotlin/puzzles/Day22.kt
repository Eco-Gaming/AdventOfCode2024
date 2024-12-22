package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day22 = Day22()
    day22.solve()
}

class Day22 : Puzzle {

    private val numbers = ArrayList<Long>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day22.txt")
        numbers.addAll(input.lines().map { it.toLong() })
    }

    override fun solvePartOne(): String {
        var sum = 0L
        for (num in numbers) {
            var num2 = num
            for (i in 1..2000) {
                num2 = evolve(num2)
            }
            sum += num2
        }
        return sum.toString()
    }

    private fun evolve(number: Long): Long {
        var out = evolveHelper(number) { it * 64 }
        out = evolveHelper(out) { it / 32 }
        out = evolveHelper(out) { it * 2048 }
        return out
    }

    private fun evolveHelper(number: Long, operation: (Long) -> Long): Long {
        var newNumber = operation(number)
        newNumber = newNumber xor number
        newNumber = newNumber.mod(16777216L)
        return newNumber
    }

    override fun solvePartTwo(): String {
        return "Not implemented"
    }
}