package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import java.util.StringJoiner
import kotlin.math.pow

fun main() {
    val day17 = Day17()
    day17.solve()
}

class Day17 : Puzzle {

    private var registerA = 0
    private var registerB = 0
    private var registerC = 0

    private val inputList = mutableListOf<Int>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day17.txt")
        val lines = input.lines().toMutableList()

        registerA = lines.removeFirst().split(": ")[1].toInt()
        registerB = lines.removeFirst().split(": ")[1].toInt()
        registerC = lines.removeFirst().split(": ")[1].toInt()

        lines.removeFirst() // empty line

        val operationList = lines.removeFirst().split(": ")[1].split(",")
        inputList.addAll(operationList.map { it.toInt() })
    }

    override fun solvePartOne(): String {
        return run(registerA, registerB, registerC, inputList)
    }

    private fun run(pA: Int, pB: Int, pC: Int, operations: List<Int>): String {
        val sj = StringJoiner(",")
        val max = inputList.size - 2 // last possible opCode

        // registers
        var a = pA
        var b = pB
        var c = pC

        var i = 0
        while (i <= max) {
            val opCode = inputList[i]
            val literalOperand = inputList[i + 1]
            val comboOperand = when (literalOperand) {
                0, 1, 2, 3 -> literalOperand
                4 -> a
                5 -> b
                6 -> c
                else -> throw IllegalArgumentException("Invalid operand code: $literalOperand")
            }

            when (opCode) {
                // adv: division
                0 -> a = (a / 2.0.pow(comboOperand)).toInt()
                // bxl: XOR
                1 -> b = b xor literalOperand
                // bst: modulo 8
                2 -> b = comboOperand % 8
                // jnz: jump
                3 -> {
                    if (a != 0) {
                        i = literalOperand
                        continue // so that `i` won't be increased later
                    }
                }
                // bxc: XOR
                4 -> b = b xor c
                // out:
                5 -> sj.add((comboOperand % 8).toString())
                // bdv: division
                6 -> b = (a / 2.0.pow(comboOperand)).toInt()
                // cdv: division
                7 -> c = (a / 2.0.pow(comboOperand)).toInt()
            }
            i += 2
        }
        return sj.toString()
    }

    override fun solvePartTwo(): String {
        TODO("Not yet implemented")
    }
}