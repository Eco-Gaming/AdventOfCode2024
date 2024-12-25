package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day25 = Day25()
    day25.solve()
}

class Day25 : Puzzle {

    private val keys = ArrayList<Array<Int>>()
    private val locks = ArrayList<Array<Int>>()

    private val maxHeight = 5

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day25.txt")
        for (block in input.split("\n\n")) {
            var blockCut = block.lines()
            var list = keys
            when (blockCut[0]) {
                "#####" -> {
                    list = locks
                    blockCut = blockCut.drop(1)
                }
                "....." -> {
                    list = keys
                    blockCut = blockCut.dropLast(1)
                }
            }
            val arr = Array(blockCut.size - 1) { 0 }
            for (i in blockCut.indices) {
                for (j in blockCut[i].indices) {
                    if (blockCut[i][j] == '#') {
                        arr[j]++
                    }
                }
            }
            list.add(arr)
        }
    }

    override fun solvePartOne(): String {
        val pairs = HashSet<Pair<Array<Int>, Array<Int>>>()
        for (lock in locks) {
            for (key in keys) {
                val newKey = Array(key.size) { 0 }
                for (i in key.indices) {
                    newKey[i] = key[i] + lock[i]
                }

                if (newKey.all { it <= maxHeight }) {
                    pairs.add(Pair(key, lock))
                }
            }
        }
        val sum = pairs.size
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        return "Not implemented"
    }
}