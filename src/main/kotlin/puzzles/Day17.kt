package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.longPow
import me.eco_gaming.readInputFromFile
import java.util.StringJoiner
import java.util.stream.LongStream
import kotlin.math.pow

fun main() {
    val day17 = Day17()
    day17.solve()
}

class Day17 : Puzzle {

    private var registerA = 0L
    private var registerB = 0L
    private var registerC = 0L

    private val inputList = mutableListOf<Int>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day17.txt")
        val lines = input.lines().toMutableList()

        registerA = lines.removeFirst().split(": ")[1].toLong()
        registerB = lines.removeFirst().split(": ")[1].toLong()
        registerC = lines.removeFirst().split(": ")[1].toLong()

        lines.removeFirst() // empty line

        val operationList = lines.removeFirst().split(": ")[1].split(",")
        inputList.addAll(operationList.map { it.toInt() })
    }

    override fun solvePartOne(): String {
        return run(registerA, registerB, registerC, inputList)
    }

    private fun run(pA: Long, pB: Long, pC: Long, operations: List<Int>, maxLength: Int = Int.MAX_VALUE): String {
        val sj = StringJoiner(",")
        val max = inputList.size - 2 // last possible opCode

        // registers
        var a = pA
        var b = pB
        var c = pC

        var i = 0
        while (i <= max) {
            if (sj.toString().length > maxLength) return ""

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
                0 -> a = (a / longPow(2L, comboOperand.toLong()))
                // bxl: XOR
                1 -> b = b xor literalOperand.toLong()
                // bst: modulo 8
                2 -> b = (comboOperand.toDouble() % 8).toLong()
                // jnz: jump
                3 -> {
                    if (a != 0L) {
                        i = literalOperand
                        continue // so that `i` won't be increased later
                    }
                }
                // bxc: XOR
                4 -> b = b xor c
                // out:
                5 -> {
                    sj.add((comboOperand.toDouble() % 8).toInt().toString())
                }
                // bdv: division
                6 -> b = (a / longPow(2, comboOperand.toLong()))
                // cdv: division
                7 -> c = (a / longPow(2, comboOperand.toLong()))
            }
            i += 2
        }
        return sj.toString()
    }

    override fun solvePartTwo(): String {
        return "Not implemented."

        // semi brute force, doesn't seem effective...

        val expectedOutput = inputList.distinct().toMutableList()
        val registerList = ArrayList<Long>()
        val expectedLength = expectedOutput.joinToString(",").length

        // find range around 8^16, where the whole algorithm would be run exactly 16 times (output length)
        // x / 8^16 = 1 => would be exactly 1 too much
        // x / 8^15 < 1 => would be  too little

        // find an x in this range, where the first output is the expected output
        // start: 35184372088832L
        // end : 281474976710656L

        // find the lowest input, for an output of expectedOutput.last()
        var i = 0L
        while (expectedOutput.isNotEmpty()) {
            val output = run(i, registerB, registerC, inputList, 1)
            if (output == expectedOutput.last().toString()) {
                registerList.add(i)
                expectedOutput.removeLast()
                println("A: $i for output: $output")
                i = 0
            } else {
                i++
            }
        }

        println(registerList)

        return ""
    }
}