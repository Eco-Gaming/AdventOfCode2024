package me.eco_gaming.puzzles

import me.eco_gaming.LogicGate
import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import java.util.stream.IntStream

fun main() {
    val day24 = Day24()
    day24.solve()
}

class Day24 : Puzzle {

    private val wireMap = HashMap<String, Boolean>()
    private val gateList = ArrayList<LogicGate>()

    private var actual = 0L // cache for part2

    private val operatorMap = hashMapOf(
        "AND" to { a: Boolean, b: Boolean -> a && b },
        "OR" to { a: Boolean, b: Boolean -> a || b },
        "XOR" to { a: Boolean, b: Boolean -> a xor b },
    )

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day24.txt")
        var modeSwitch = false
        for (line in input.lines()) {
            if (line.isBlank()) {
                modeSwitch = true
                continue
            }
            if (!modeSwitch) {
                val split = line.split(": ")
                wireMap[split[0]] = (split[1] == "1")
            } else {
                val split = line.split(" -> ")
                val input2 = split[0].split(" ")
                val output = split[1]

                gateList.add(
                    LogicGate(
                        input2[0],
                        input2[2],
                        output,
                        operatorMap[input2[1]]!!,
                        input2[1],
                    )
                )
            }
        }
    }

    override fun solvePartOne(): String {
        val wireMapCopy = wireMap.toMutableMap()
        val gateListCopy = gateList.toMutableList()
        while (gateListCopy.isNotEmpty()) {
            val gate = gateListCopy.firstOrNull { it.evaluate(wireMapCopy) }
            if (gate != null) {
                gateListCopy.remove(gate)
            } else {
                break
            }
        }
        val out = getNumber(wireMapCopy)
        actual = out // cache for part2
        return out.toString()
    }

    private fun getNumber(wireMapCopy: Map<String, Boolean>, prefix: String = "z"): Long {
        val binaryStringBuilder = StringBuilder()
        wireMapCopy.filter { it.key.startsWith(prefix) }
            .toSortedMap().reversed()
            .forEach { (_, value) -> binaryStringBuilder.append(if (value) "1" else "0") }
        return binaryStringBuilder.toString().toLong(2)
    }

    override fun solvePartTwo(): String {

        // reverse engineer easy to spot faulty gates
//        val allGates = IntStream.rangeClosed(0, 45).mapToObj { "z%02d".format(it) }.toList()
//        for (gate in allGates) {
//            val logicExpression = reverse(gate, 3)
//            val gateString1 = "(%s XOR %s)".format(gate.replace("z", "x"), gate.replace("z", "y"))
//            val gateString2 = "(%s XOR %s)".format(gate.replace("z", "y"), gate.replace("z", "x"))
//            // if the gates don't directly depend on input1 XOR input2, they have to be faulty
//            if (!logicExpression.contains(gateString1) && !logicExpression.contains(gateString2)) {
//                println("$gate: $logicExpression")
//            }
//        }

        // afterward, reverse engineer all wrong bits and use the formulas
        // for s_i and c_i of a full adder to swap gate output
        // println("z20: " + reverse("z20", 4))

        val num1 = getNumber(wireMap, "x")
        val num2 = getNumber(wireMap, "y")
        val expected = num1 + num2

//        println("0" + num1.toString(2))
//        println("0" + num2.toString(2))
//
//        println("+ ------------")
//
//        println(expected.toString(2) + " = expected")
//        println(actual.toString(2) + " = actual")

        return "solved manually"
    }

    // recursively reverse the path for a given output
    // used to manually find deviations to the correct formulas for a full adder
    private fun reverse(gate: String, depth: Int): String {
        if (depth == 0) return gate

        val gates = gateList.first { it.output == gate }
        val out = StringBuilder()

        if (gates.input1.startsWith("x") || gates.input1.startsWith("y")) {
            out.append(gates.input1)
        } else {
            out.append("(")
            out.append(reverse(gates.input1, depth - 1))
            out.append(")")
        }

        out.append(" ${gates.operatorName} ")

        if (gates.input2.startsWith("x") || gates.input2.startsWith("y")) {
            out.append(gates.input2)
        } else {
            out.append("(")
            out.append(reverse(gates.input2, depth - 1))
            out.append(")")
        }

        return out.toString()
    }
}