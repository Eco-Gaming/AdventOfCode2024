package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import me.eco_gaming.utils.ClawMachine

fun main() {
    val day13 = Day13()
    day13.solve()
}

class Day13 : Puzzle {

    private val machineList = ArrayList<ClawMachine>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day13.txt")
        for (group in input.split("\n\n")) {
            val lines = group.lines()
            val a = lines[0].split(", ")
            val b = lines[1].split(", ")
            val prize = lines[2].split(", ")
            machineList.add(ClawMachine(
                a[0].split("X")[1].toDouble(), a[1].split("Y")[1].toDouble(),
                b[0].split("X")[1].toDouble(), b[1].split("Y")[1].toDouble(),
                prize[0].split("X=")[1].toDouble(), prize[1].split("Y=")[1].toDouble(),
            ))
        }
    }

    override fun solvePartOne(): String {
        var sum = 0L
        for (machine in machineList) {
            val solution = machine.solveEquation()
            if (solution != null) {
                // A: 3 tokens
                // B: 1 token
                sum += (solution.first * 3) + solution.second
            }
        }
        return sum.toString()
    }

    override fun solvePartTwo(): String {
        var sum = 0L
        for (machine in machineList) {
            val solution = machine.solveEquation(10000000000000, false)
            if (solution != null) {
                // A: 3 tokens
                // B: 1 token
                sum += (solution.first * 3) + solution.second
            }
        }
        return sum.toString()
    }
}