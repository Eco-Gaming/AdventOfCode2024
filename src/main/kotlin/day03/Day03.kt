package me.eco_gaming.day03

import java.io.BufferedReader
import java.io.File

fun main() {
    val input = readInputFromFile("src/main/resources/day03.txt")
    val mulList = getMulList(input)
    val sum = calcSum(mulList)
    println(sum)
}

private fun readInputFromFile(filePath: String): String {
    val bufferedReader: BufferedReader = File(filePath).bufferedReader()
    return bufferedReader.use { it.readText() }
}

private fun getMulList(mulString: String): List<String> {
    val mulList = mutableListOf<String>()

    var i = 0
    while (i < mulString.length) {
        val startIndex = mulString.indexOf("mul(", i)
        if (startIndex == -1) break

        val bracketIndex = mulString.indexOf(')', startIndex)
        if (bracketIndex == -1) break

        val extractedString = mulString.substring(startIndex, bracketIndex + 1)

        // if the found match is longer than mul(XXX,XXX)
        if (bracketIndex > startIndex + 11) {
            i = startIndex + 1
            continue
        }

        if (validateString(extractedString)) mulList.add(extractedString)

        i = bracketIndex + 1
    }

    return mulList
}

private fun validateString(str: String): Boolean {
    val strArr = str.substring(4, str.length - 1).split(",")
    if (strArr.size != 2) return false
    if (strArr.none { it.any { char -> char.isDigit() } } ||
        strArr[0].isEmpty() || strArr[0].length > 3 || strArr[1].isEmpty() || strArr[1].length > 3) return false
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
