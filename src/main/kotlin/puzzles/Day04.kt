package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day04 = Day04()
    day04.solve()
}

class Day04 : Puzzle {

    private val matrix: MutableList<List<Char>> = mutableListOf()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day04.txt")
        val lines = input.lines()

        for (line in lines) {
            matrix.add(strToIntList(line))
        }
    }

    private fun strToIntList(str: String): MutableList<Char> {
        val list = mutableListOf<Char>()
        for (c in str) {
            list.add(c)
        }
        return list
    }

    override fun solvePartOne(): String {
        var sum = 0
        for (line in matrix) {
            // forward row
            sum += countStr(line.joinToString(""))
            // backward row
            sum += countStr(line.reversed().joinToString(""))
        }
        for (column in getMatrixColumns(matrix)) {
            // forward column
            sum += countStr(column)
            // backward column
            sum += countStr(column.reversed())
        }
        for (diagonal in getMatrixDiagonals(matrix)) {
            // forward diagonal
            sum += countStr(diagonal)
            // backward diagonal
            sum += countStr(diagonal.reversed())
        }
        return sum.toString()
    }

    private fun countStr(input: String): Int {
        var sum = 0
        var index = input.indexOf("XMAS")
        while (index >= 0) {
            sum++
            index = input.indexOf("XMAS", index + 1)
        }
        return sum
    }

    private fun getMatrixColumns(input: List<List<Char>>): List<String> {
        val list = mutableListOf<String>()
        for (j in input[0].indices) {
            val column = StringBuilder()
            for (i in input[j].indices) {
                column.append(input[i][j])
            }
            list.add(column.toString())
        }
        return list
    }

    private fun getMatrixDiagonals(input: List<List<Char>>): List<String> {
        val list = mutableListOf<String>()
        // diagonals top left to bottom right
        var offset = 0
        for (j in input[0].indices) {
            val diagonalOne = StringBuilder()
            for (i in input.indices) {
                val newJ = j + offset
                try {
                    diagonalOne.append(input[i][newJ])
                } catch (e: IndexOutOfBoundsException) {
                    break
                }
                offset++
            }
            offset = 0
            list.add(diagonalOne.toString())
        }
        var offset2 = 0
        for (i in input.indices) {
            if (i == 0) continue
            val diagonalOneRest = StringBuilder()
            for (j in input[i].indices) {
                try {
                    diagonalOneRest.append(input[i + offset2][j])
                } catch (e: IndexOutOfBoundsException) {
                    break
                }
                offset2++
            }
            offset2 = 0
            list.add(diagonalOneRest.toString())
        }

        // diagonals top right to bottom left
        var offset3 = 0
        for (j in input[0].indices.reversed()) {
            val diagonalTwo = StringBuilder()
            for (i in input.indices) {
                val newJ = j - offset3
                try {
                    diagonalTwo.append(input[i][newJ])
                } catch (e: IndexOutOfBoundsException) {
                    break
                }
                offset3++
            }
            offset3 = 0
            list.add(diagonalTwo.toString())
        }
        var offset4 = 0
        for (i in input.indices) {
            if (i == 0) continue
            val diagonalTwoRest = StringBuilder()
            for (j in input[i].indices.reversed()) {
                try {
                    diagonalTwoRest.append(input[i + offset4][j])
                } catch (e: IndexOutOfBoundsException) {
                    break
                }
                offset4++
            }
            offset4 = 0
            list.add(diagonalTwoRest.toString())
        }

        return list
    }

    override fun solvePartTwo(): String {
        // find all 'A'
        val aIndices = mutableListOf<Pair<Int, Int>>()
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == 'A') aIndices.add(Pair(i, j))
            }
        }
        // search for 'M' on diagonal
        // search for another 'M' clockwise and counterclockwise
        // then search for 2 'A' in the same direction
        var sum = 0
        for (pair in aIndices) {
            if (pair.first !in 1..matrix.size - 2) continue
            if (pair.second !in 1..matrix[pair.first].size - 2) continue

            val contenders = mutableListOf<Char>()
            contenders.add(matrix[pair.first - 1][pair.second - 1]) // top left
            contenders.add(matrix[pair.first - 1][pair.second + 1]) // top right
            contenders.add(matrix[pair.first + 1][pair.second + 1]) // bottom right
            contenders.add(matrix[pair.first + 1][pair.second - 1]) // bottom left

            if (contenders.any { char -> (char != 'M' && char != 'S') } ) continue

            if (contenders[0] == 'M' && ((contenders[1] == 'M') xor (contenders[3] == 'M'))) {
                if (contenders.count { char -> char == 'S' } == 2) sum++
                continue
            }

            if (contenders[0] == 'S' && ((contenders[1] == 'S') xor (contenders[3] == 'S'))) {
                if (contenders.count { char -> char == 'M' } == 2) sum++
                continue
            }
        }
        return sum.toString()
    }
}