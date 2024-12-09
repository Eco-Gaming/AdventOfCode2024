package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day09 = Day09()
    day09.solve()
}

class Day09 : Puzzle {

    private lateinit var inputArray: Array<Int>

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day09.txt")
        inputArray = input.map { c -> c.digitToInt() }.toTypedArray()
    }

    override fun solvePartOne(): String {
        val expandedList = expandArray(inputArray)
        val compactedList = compactList(expandedList)
        val checksum = calculateChecksum(compactedList)
        return checksum.toString()
    }

    override fun solvePartTwo(): String {
        val expandedList = expandArrayToTriples(inputArray)
        val compactedList = compactTriples(expandedList)
        val checkSum = calculateTripleChecksum(compactedList)
        return checkSum.toString()
    }

    private fun expandArray(array: Array<Int>): List<Int> {
        val output = ArrayList<Int>()
        for (i in array.indices) {
            for (j in 0..<array[i]) {
                if (i % 2 == 0) { // block storage
                    output.add(i / 2)
                } else { // free space
                    output.add(-1)
                }
            }
        }
        return output
    }

    private fun compactList(input: List<Int>): List<Int> {
        val array = input.toMutableList()
        var i = array.size - 1
        var i2 = array.indexOf(-1)
        while (i2 >= 0 && i >= 0 && i2 <= i) {
            while (i2 < array.size && array[i2] == -1 && array[i] != -1) {
                array[i2] = array[i]
                array[i] = -1
                i2++
                i--
            }
            if (i2 == array.size) break
            if (array[i] == -1) i = array.indexOfLast { it != -1 }
            if (array[i2] != -1) i2 += array.subList(i2 + 1, array.size).indexOf(-1) + 1
        }
        return array
    }

    private fun calculateChecksum(array: List<Int>): Long {
        var sum = 0L
        for (i in array.indices) {
            if (array[i] < 0) continue
            sum += array[i] * i
        }
        return sum
    }

    // Triple<ID, startIndex, length>
    private fun expandArrayToTriples(array: Array<Int>): List<Triple<Int, Int, Int>> {
        val output = ArrayList<Triple<Int, Int, Int>>()
        output.add(Triple(0, 0, 0))
        for (i in array.indices) {
            if (i % 2 == 0) {
                output.add(Triple(i / 2, output.last().second + output.last().third, array[i]))
            } else {
                output.add(Triple(-1, output.last().second + output.last().third, array[i]))
            }
        }
        output.removeFirst()
        return output
    }

    private fun compactTriples(triples: List<Triple<Int, Int, Int>>): List<Triple<Int, Int, Int>> {
        val output = triples.toMutableList()
        for (i in output.indices.reversed()) {
            if (output[i].first == -1) continue
            for (j in 0..<i) {
                if (output[j].first == -1 && output[j].third >= output[i].third) {
                    val temp = output[j]
                    val rightStartIndex = output[i].second
                    output[j] = output[i]
                    output[j] = Triple(output[j].first, temp.second, output[j].third)
                    if (output[j].third == temp.third) {
                        output[i] = temp
                        output[i] = Triple(output[i].first, rightStartIndex, output[i].third)
                    } else {
                        output.add(j + 1, Triple(-1, output[j].second + output[j].third, temp.third - output[j].third))
                        output[i + 1] = Triple(-1, rightStartIndex, output[j].third)
                    }
                    break
                }
            }
        }
        return output
    }

    private fun calculateTripleChecksum(triples: List<Triple<Int, Int, Int>>): Long {
        var sum = 0L
        for (triple in triples) {
            if (triple.first > -1) {
                for (i in 0..<triple.third) {
                    sum += triple.first * (triple.second + i)
                }
            }
        }
        return sum
    }
}