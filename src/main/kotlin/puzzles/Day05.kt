package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day05 = Day05()
    day05.solve()
}

class Day05 : Puzzle {

    private val ruleMap: MutableMap<Int, MutableList<Int>> = HashMap()
    private val inputList: MutableList<List<Int>> = ArrayList()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day05.txt")
        var modeSwitch = false
        for (line in input.lines()) {
            if (line.isBlank()) {
                modeSwitch = true
                continue
            }
            if (!modeSwitch) {
                val lineArr = line.split("|").map { it.toInt() }
                if (!ruleMap.containsKey(lineArr[0])) {
                    ruleMap[lineArr[0]] = mutableListOf(lineArr[1])
                } else {
                    ruleMap[lineArr[0]]?.add(lineArr[1])
                }
            } else {
                inputList.add(line.split(",").map { it.toInt() })
            }
        }
        return
    }

    override fun solvePartOne(): String {
        var sum = 0
        lines@ for (line in inputList) {
            for (rule in ruleMap.keys) {
                if (line.contains(rule)) {
                    val index = line.indexOf(rule)
                    val compliant = ruleMap[rule]?.all { value ->
                        if (line.contains(value)) {
                            line.indexOf(value) > index
                        } else {
                            true
                        }
                    }
                    if (!compliant!!) {
                        continue@lines
                    }
                }
            }
            val middlePageNumber = line[(line.size - 1) / 2]
            sum += middlePageNumber
        }
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        TODO("Not yet implemented")
    }
}