package me.eco_gaming

import java.awt.Point
import java.io.BufferedReader
import java.io.File
import java.util.*

fun readInputFromFile(filePath: String): String {
    val bufferedReader: BufferedReader = File(filePath).bufferedReader()
    return bufferedReader.use { it.readText() }
}

enum class Facing {
    NORTH, EAST, SOUTH, WEST;

    fun rotateClockwise(): Facing {
        return when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
    }
}

class GuardPosition(var x: Int, var y: Int, var facing: Facing) {

    fun step(matrix: List<List<Boolean>>) {
        val nextPosition = GuardPosition(x, y, facing)
        when (facing) {
            Facing.NORTH -> nextPosition.x--
            Facing.EAST -> nextPosition.y++
            Facing.SOUTH -> nextPosition.x++
            Facing.WEST -> nextPosition.y--
        }
        try {
            if (!matrix[nextPosition.x][nextPosition.y]) {
                x = nextPosition.x
                y = nextPosition.y
            } else {
                facing = facing.rotateClockwise()
            }
        } catch (e: IndexOutOfBoundsException) {
            x = -1
            y = -1
            facing = Facing.NORTH
        }
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is GuardPosition -> this.x == other.x && this.y == other.y && this.facing == other.facing
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y, facing)
    }
}

enum class Operator {
    PLUS, TIMES, CONCAT
}

class ListEntry(var char: Char, var area: Int, var perimeter: Int, val points: MutableList<Point>) {
    //
}

class Robot(var x: Int, var y: Int, val dX: Int, val dY: Int) {

    fun step(dimensionX: Int, dimensionY: Int, count: Int = 1) {
        x = (x + dX*count).mod(dimensionX + 1)
        y = (y + dY*count).mod(dimensionY + 1)
    }

    fun copy(): Robot {
        return Robot(x, y, dX, dY)
    }
}

class Day15Robot(var x: Int, var y: Int) {

    fun step(matrix: List<MutableList<Char>>, direction: Facing) {
        var newX = x
        var newY = y
        when (direction) {
            Facing.NORTH -> newX--
            Facing.EAST -> newY++
            Facing.SOUTH -> newX++
            Facing.WEST -> newY--
        }

        when (matrix[newX][newY]) {
            '.' -> { // if the space is empty, we move into it
                x = newX
                y = newY
            }
            '#' -> {} // if we would move into a wall, we do nothing
            'O' -> { // if we move into a box, we attempt pushing it
                // check if there is a dot "remaining" in the row or column of the box
                when (direction) {
                    Facing.NORTH -> {
                        val contenders = List(matrix.size) { matrix[it][y] }.subList(0, x)
                        val contender = contenders.indexOfLast { it == '.' }
                        val contenderLimit = contenders.indexOfLast { it == '#' }
                        if (contender != -1 && contenderLimit < contender) {
                            // swap dot with box
                            matrix[contender][y] = 'O'
                            matrix[newX][newY] = '.'
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                    Facing.EAST -> {
                        val contenders = matrix[x].subList(y + 1, matrix[x].size)
                        val contender = contenders.indexOfFirst { it == '.' }
                        val contenderLimit = contenders.indexOfFirst { it == '#' }
                        if (contender != -1 && contenderLimit > contender) {
                            // swap dot with box
                            matrix[x][y + 1 + contender] = 'O'
                            matrix[newX][newY] = '.'
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                    Facing.SOUTH -> {
                        val contenders = List(matrix.size) { matrix[it][y] }.subList(x + 1, matrix.size)
                        val contender = contenders.indexOfFirst { it == '.' }
                        val contenderLimit = contenders.indexOfFirst { it == '#' }
                        if (contender != -1 && contenderLimit > contender) {
                            // swap dot with box
                            matrix[x + 1 + contender][y] = 'O'
                            matrix[newX][newY] = '.'
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                    Facing.WEST -> {
                        val contenders = matrix[x].subList(0, y)
                        val contender = contenders.indexOfLast { it == '.' }
                        val contenderLimit = contenders.indexOfLast { it == '#' }
                        if (contender != -1 && contenderLimit < contender) {
                            // swap dot with box
                            matrix[x][contender] = 'O'
                            matrix[newX][newY] = '.'
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                }
            }
        }
    }

    fun stepPartTwo(matrix: List<MutableList<Char>>, direction: Facing) {
        var newX = x
        var newY = y
        when (direction) {
            Facing.NORTH -> newX--
            Facing.EAST -> newY++
            Facing.SOUTH -> newX++
            Facing.WEST -> newY--
        }

        when (matrix[newX][newY]) {
            '.' -> { // if the space is empty, we move into it
                x = newX
                y = newY
            }
            '#' -> {} // if we would move into a wall, we do nothing
            '[', ']' -> { // if we move into a box, we attempt pushing it
                // check if there is space remaining in the row or column of the box
                when (direction) {
                    Facing.NORTH -> {
                        val contenders = List(matrix.size) { matrix[it][y] }.subList(0, x)
                        val contender = contenders.indexOfLast { it == '.' }
                        val contenderLimit = contenders.indexOfLast { it == '#' }
                        if (contender != -1 && contenderLimit < contender) {
                            // move box(es) and empty space north
                            val boxes = ArrayList<Pair<Point, Point>>() // (x, y)
                            for (i in (contender..newX).reversed()) {
                                when (matrix[i][y]) {
                                    '[' -> boxes.add(Pair(Point(i, y), Point(i, y + 1)))
                                    ']' -> boxes.add(Pair(Point(i, y - 1), Point(i, y)))
                                }
                            }
                            if (boxes.all { canBoxMove(it, matrix, direction) }) {
                                for (box in boxes) {
                                    matrix[box.first.x - 1][box.first.y] = matrix[box.first.x][box.first.y]
                                    matrix[box.second.x - 1][box.second.y] = matrix[box.second.x][box.second.y]
                                }
                                // move robot to newX and newY
                                x = newX
                                y = newY
                            }
                        }
                    }
                    Facing.EAST -> {
                        val contenders = matrix[x].subList(y + 1, matrix[x].size)
                        val contender = contenders.indexOfFirst { it == '.' }
                        val contenderLimit = contenders.indexOfFirst { it == '#' }
                        if (contender != -1 && contenderLimit > contender) {
                            // move box(es) and empty space east
                            for (i in (newY..contender).reversed()) {
                                matrix[x][i] = matrix[x][i - 1]
                            }
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                    Facing.SOUTH -> {
                        val contenders = List(matrix.size) { matrix[it][y] }.subList(x + 1, matrix.size)
                        val contender = contenders.indexOfFirst { it == '.' }
                        val contenderLimit = contenders.indexOfFirst { it == '#' }
                        if (contender != -1 && contenderLimit > contender) {
                            // swap dot with box
                            matrix[x + 1 + contender][y] = 'O'
                            matrix[newX][newY] = '.'
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                    Facing.WEST -> {
                        val contenders = matrix[x].subList(0, y)
                        val contender = contenders.indexOfLast { it == '.' }
                        val contenderLimit = contenders.indexOfLast { it == '#' }
                        if (contender != -1 && contenderLimit < contender) {
                            // move box(es) and empty space west
                            for (i in contender..newY) {
                                matrix[x][i] = matrix[x][i + 1]
                            }
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                }
            }
        }
    }

    fun copy(): Day15Robot {
        return Day15Robot(x, y)
    }
}

// only defined for facing north and south !!!
fun canBoxMove(box: Pair<Point, Point>, matrix: List<List<Char>>, direction: Facing): Boolean {
    return when (direction) {
        Facing.NORTH -> {
            val contenders = listOf(matrix[box.first.x - 1][box.first.y], matrix[box.second.x - 1][box.second.y])
            contenders.any { it == '#' } || contenders.all { it == '.' } ||
        }
        Facing.EAST -> false
        Facing.SOUTH -> {
            val contenders = listOf(matrix[box.first.x + 1][box.first.y], matrix[box.second.x + 1][box.second.y])
            contenders.all { it == '.' }
        }
        Facing.WEST -> false
    }
}