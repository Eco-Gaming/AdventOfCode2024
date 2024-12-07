package me.eco_gaming.puzzles

import me.eco_gaming.Operator
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
        val operators = listOf(Operator.PLUS, Operator.TIMES)
        for (equation in equations) {
            if (isSolvable(equation.first, equation.second, operators)) {
                sum += equation.first
            }
        }
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        var sum: Long = 0
        val operators = listOf(Operator.PLUS, Operator.TIMES, Operator.CONCAT)
        for (equation in equations) {
            if (isSolvable(equation.first, equation.second, operators)) {
                sum += equation.first
            }
        }
        return sum.toString()
    }

    private fun isSolvable(result: Long, list: List<Long>, operators: List<Operator>): Boolean {
        val lastElement = list.last()

        if (list.size == 1) {
            return lastElement == result
        }

        val canDivide = operators.contains(Operator.TIMES) && result.isDivisibleBy(lastElement)
        val canSubtract = operators.contains(Operator.PLUS) && result - lastElement > 0
        val canSplit = operators.contains(Operator.CONCAT) && result > lastElement
        val splitResult = if (canSplit) {
            var splitLength = 10L
            while (splitLength <= lastElement) splitLength *= 10L
            if (result % splitLength == lastElement) result / splitLength else null
        } else {
            null
        }

        return (canDivide && isSolvable(result / lastElement, list.dropLast(1), operators)) ||
                (canSubtract && isSolvable(result - lastElement, list.dropLast(1), operators)) ||
                (splitResult != null && isSolvable(splitResult, list.dropLast(1), operators))
    }
}