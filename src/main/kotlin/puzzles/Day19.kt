package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day19 = Day19()
    day19.solve()
}

class Day19 : Puzzle {

    private val towels = ArrayList<String>()
    private val patterns = ArrayList<String>()

    private val patternCache = HashMap<String, Long>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day19.txt")
        val lines = input.lines().toMutableList()

        towels.addAll(lines.removeFirst().split(", "))

        lines.removeFirst() // empty line

        patterns.addAll(lines)
    }

    override fun solvePartOne(): String {
        var sum = 0
        for (pattern in patterns) {
            if (fits(pattern)) {
                sum++
            }
        }
        return sum.toString()
    }

    private fun fits(pattern: String): Boolean {
        val contenders = ArrayList<String>()
        for (towel in towels) {
            if (towel == pattern) return true
            if (pattern.length >= towel.length && pattern.startsWith(towel)) {
                contenders.add(pattern.substring(towel.length))
            }
        }
        if (contenders.isEmpty()) {
            return false
        }
        for (contender in contenders) {
            if (fits(contender)) {
                return true
            }
        }
        return false
    }

    override fun solvePartTwo(): String {
        var sum = 0L
        for (pattern in patterns) {
            sum += fitCount(pattern)
        }
        return sum.toString()
    }

    private fun fitCount(pattern: String): Long {
        if (patternCache.containsKey(pattern)) {
            return patternCache[pattern]!!
        }

        val contenders = ArrayList<String>()
        var sum = 0L
        for (towel in towels) {
            if (towel == pattern) {
                sum++
                continue
            }
            if (pattern.length >= towel.length && pattern.startsWith(towel)) {
                contenders.add(pattern.substring(towel.length))
            }
        }
        if (contenders.isEmpty()) return sum

        val newSum = contenders.sumOf { fitCount(it) } + sum
        patternCache[pattern] = newSum
        return newSum
    }
}