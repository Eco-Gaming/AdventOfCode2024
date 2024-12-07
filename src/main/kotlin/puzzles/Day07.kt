package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day07 = Day07()
    day07.solve()
}

infix fun Long.isDivisibleBy(divisor: Long): Boolean {
    return this % divisor == 0.toLong()
}

class Day07 : Puzzle {

    private val equations = ArrayList<Pair<Long, List<Long>>>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day07.txt")
        for (line in input.lines()) {
            val split = line.split(": ")
            equations.add(Pair(split[0].toLong(), split[1].split(" ").toList().map(String::toLong)))
        }
    }

    override fun solvePartOne(): String {
        var sum: Long = 0
        for (equation in equations) {
            if (isSolvable(equation.first, equation.second)) {
                sum += equation.first
            }
        }
        return sum.toString()
    }

    private fun isSolvable(result: Long, list: List<Long>): Boolean {
        val lastElement = list.last()

        if (list.size == 1) {
            return lastElement == result
        }

        val canDivide = result.isDivisibleBy(lastElement)
        val canSubtract = result - lastElement > 0

        return (canDivide && isSolvable(result / lastElement, list.dropLast(1))) ||
                (canSubtract && isSolvable(result - lastElement, list.dropLast(1)))
    }

    override fun solvePartTwo(): String {
        TODO("Not yet implemented")
    }
}