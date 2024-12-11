package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
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
        return getList(75).size.toString()
    }

    private fun getList(iterations: Int): List<Long> {
        // (stone, iterations left), resulting stones
        val cache = HashMap<Pair<Long, Int>, List<Long>>()
        val list = ArrayList<Long>()
        for (stone in inputList) {
            list.addAll(expandStone(stone, iterations, cache))
        }
        return list
    }

    private fun expandStone(stone: Long, iterations: Int, cacheMap: HashMap<Pair<Long, Int>, List<Long>>): List<Long> {
        if (iterations == 0) return listOf(stone)

        val cache = cacheMap[Pair(stone, iterations)]
        if (cache != null) return cache

        val result = when (stone) {
            0L -> expandStone(1, iterations - 1, cacheMap)
            else -> {
                val digits = countDigits(stone)
                if (digits % 2 == 0L) {
                    val left = stone / 10.0.pow(digits / 2.0).toInt()
                    val right = stone % 10.0.pow(digits / 2.0).toInt()
                    expandStone(left, iterations - 1, cacheMap) + expandStone(right, iterations - 1, cacheMap)
                } else {
                    expandStone(stone * 2024, iterations - 1, cacheMap)
                }
            }
        }
        cacheMap[Pair(stone, iterations)] = result
        return result
    }

    private fun countDigits(input: Long): Long {
        return input.toString().length.toLong()
    }
}