package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.longPow
import me.eco_gaming.readInputFromFile

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
        return run(registerA, registerB, registerC, inputList).joinToString(",")
    }

    private fun run(pA: Long, pB: Long, pC: Long, operations: List<Int>): List<Int> {
        val list = mutableListOf<Int>()
        val max = operations.size - 2 // last possible opCode

        // registers
        var a = pA
        var b = pB
        var c = pC

        var i = 0
        while (i <= max) {
            val opCode = operations[i]
            val literalOperand = operations[i + 1]
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
                    list.add((comboOperand.toDouble() % 8).toInt())
                }
                // bdv: division
                6 -> b = (a / longPow(2, comboOperand.toLong()))
                // cdv: division
                7 -> c = (a / longPow(2, comboOperand.toLong()))
            }
            i += 2
        }
        return list
    }

    // [!] this approach may not work with all inputs [!]
    override fun solvePartTwo(): String {
        val expectedOutput = inputList.toMutableList()
        val registerList = ArrayList<String>()

        // find the lowest input for the required output, going from back to front
        // implemented with a recursive function + depth first search,
        // as there may be multiple input options for the same output
        if (findCombination(expectedOutput, registerList)) {
            return registerList.joinToString("").toLong(8).toString()
        } else {
            error("No valid combination found")
        }
    }

    private fun findCombination(expectedOutput: MutableList<Int>, registerList: MutableList<String>): Boolean {
        if (expectedOutput.isEmpty()) {
            return true
        }

        // every iteration only depends on the last bit of registerA as an octal number
        // so we can slowly build up A, going from the back of the expected output to the front
        val expected = expectedOutput.removeLast()
        for (i in 0..7) {
            val newA = buildA(registerList, i)
            val output = run(newA, registerB, registerC, inputList)
            if (output.isNotEmpty() && output[0] == expected) {
                registerList.add(i.toString())

                // depth first search
                if (findCombination(expectedOutput, registerList)) {
                    return true
                }
                registerList.removeAt(registerList.size - 1)
            }
        }
        expectedOutput.add(expected)
        return false
    }

    // builds the right value for registerA, as this is a lot easier to do with octal numbers
    private fun buildA(registerList: List<String>, i: Int): Long {
        val listCopy = registerList.toMutableList()
        listCopy.add(i.toString())
        return listCopy.joinToString("").toLong(8)
    }
}