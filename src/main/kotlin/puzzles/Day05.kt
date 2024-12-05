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
        return calcSum().toString()
    }

    override fun solvePartTwo(): String {
        return (calcSum(fixLines = true) - calcSum()).toString()
    }

    private fun calcSum(fixLines: Boolean = false): Int {
        var sum = 0
        lines@ for (line in inputList) {
            var lineVar = line
            for (rule in ruleMap.keys) {
                if (lineVar.contains(rule)) {
                    if (!isCompliant(lineVar, rule)) {
                        if (!fixLines) continue@lines
                        lineVar = fixLine(lineVar, rule)
                    }
                }
            }
            val middlePageNumber = lineVar[(lineVar.size - 1) / 2]
            sum += middlePageNumber
        }
        return sum
    }

    private fun isCompliant(line: List<Int>, rule: Int): Boolean {
        val index = line.indexOf(rule)
        return ruleMap[rule]?.all { value ->
            if (line.contains(value)) {
                line.indexOf(value) > index
            } else {
                true
            }
        } == true
    }

    private fun fixLine(line: List<Int>, rule: Int): List<Int> {
        val lineVar = line.toMutableList()
        while (!isCompliant(lineVar, rule) && lineVar.indexOf(rule) > 0) {
            val index = lineVar.indexOf(rule)
            val temp = lineVar[index - 1]
            lineVar[index - 1] = lineVar[index]
            lineVar[index] = temp
        }
        return lineVar
    }
}