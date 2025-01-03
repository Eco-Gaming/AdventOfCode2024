package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day03 = Day03()
    day03.solve()
}

class Day03 : Puzzle {

    private lateinit var input: String

    override fun readFile() {
        input = readInputFromFile("src/main/resources/day03.txt")
    }

    override fun solvePartOne(): String {
        val mulList = getMulList(input)
        return calcSum(mulList).toString()
    }

    override fun solvePartTwo(): String {
        val mulList = getMulList(input, true)
        return calcSum(mulList).toString()
    }
}

private fun getMulList(mulString: String, respectDoAndDont: Boolean = false): List<String> {
    val mulList = mutableListOf<String>()

    var dontIndex = mulString.indexOf("don't()")
    var i = 0
    while (i < mulString.length) {
        val startIndex = mulString.indexOf("mul(", i)
        if (startIndex == -1) break

        val bracketIndex = mulString.indexOf(')', startIndex)
        if (bracketIndex == -1) break

        if (respectDoAndDont && dontIndex in 0..startIndex) {
            i = mulString.indexOf("do()", i)
            if (i == -1) break
            dontIndex = mulString.indexOf("don't()", i + 1)
            continue
        }

        // if the found match is longer than mul(XXX,XXX)
        if (bracketIndex > startIndex + 11) {
            i = startIndex + 1
            continue
        }

        val extractedString = mulString.substring(startIndex, bracketIndex + 1)

        if (validateString(extractedString)) mulList.add(extractedString)

        i = bracketIndex + 1
    }

    return mulList
}

private fun validateString(str: String): Boolean {
    val strArr = str.substring(4, str.length - 1).split(",")
    if (strArr.size != 2) return false
    if (strArr.none { it.any { char -> char.isDigit() } } ||
        strArr[0].length !in 0 .. 3 || strArr[1].length !in 0 .. 3) return false
    return true
}

private fun calcSum(mulList: List<String>): Int {
    var sum = 0;
    for (s in mulList) {
        val arr = s.substring(4, s.length - 1).split(",")
        val num1 = arr[0].toInt()
        val num2 = arr[1].toInt()
        sum += num1 * num2
    }
    return sum
}
