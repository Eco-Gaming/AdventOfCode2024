package me.eco_gaming

import java.awt.Point
import java.io.BufferedReader
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

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

    fun stepPartTwo(emptyMatrix: List<List<Char>>, boxes: MutableList<Box>, direction: Facing) {
        var newX = x
        var newY = y
        when (direction) {
            Facing.NORTH -> newX--
            Facing.EAST -> newY++
            Facing.SOUTH -> newX++
            Facing.WEST -> newY--
        }

        when (emptyMatrix[newX][newY]) {
            '#' -> {} // if we would move into a wall, we do nothing
            '.' -> { // check if we would move into a box
                val boxCheck = boxes.firstOrNull { it.getY().contains(newY) && it.getX() == newX }
                if (boxCheck == null) {
                    // move robot to newX and newY
                    x = newX
                    y = newY
                    return
                }

                // check if there is space remaining in the row or column of the box
                when (direction) {
                    Facing.NORTH -> {
                        val box = boxes.first { it.getY().contains(y) && it.getX() + 1 == x }
                        if (box.move(emptyMatrix, boxes, direction)) {
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                    Facing.EAST -> {
                        val yIndices = newY..emptyMatrix[x].subList(newY, emptyMatrix[x].size).indexOf('#') - 1 + newY
                        val yBoxes = boxes.filter { it.getY().all { boxY -> boxY in yIndices } && it.getX() == x }
                        if (yIndices.isEmpty() || yBoxes.size * 2 >= yIndices.count()) {
                            // no more free space in this row
                            return
                        }
                        val moveBoxes = ArrayList<Box>()
                        for (i in yIndices) {
                            val box = boxes.firstOrNull { it.getY().contains(i) && it.getX() == x }
                            if (box != null) {
                                moveBoxes.add(box)
                            } else {
                                break
                            }
                        }
                        for (box in moveBoxes.distinct()) {
                            box.left.y++
                            box.right.y++
                        }
                        // move robot to newX and newY
                        x = newX
                        y = newY
                    }
                    Facing.SOUTH -> {
                        val box = boxes.first { it.getY().contains(y) && it.getX() - 1 == x }
                        if (box.move(emptyMatrix, boxes, direction)) {
                            // move robot to newX and newY
                            x = newX
                            y = newY
                        }
                    }
                    Facing.WEST -> {
                        val yIndices = emptyMatrix[x].subList(0, newY).lastIndexOf('#')+1..newY
                        val yBoxes = boxes.filter { it.getY().all { boxY -> boxY in yIndices } && it.getX() == x }
                        if (-1 in yIndices || yBoxes.size * 2 >= yIndices.count()) {
                            // no more free space in this row
                            return
                        }
                        val moveBoxes = ArrayList<Box>()
                        for (i in yIndices.reversed()) {
                            val box = boxes.firstOrNull { it.getY().contains(i) && it.getX() == x }
                            if (box != null) {
                                moveBoxes.add(box)
                            } else {
                                break
                            }
                        }
                        for (box in moveBoxes.distinct()) {
                            box.left.y--
                            box.right.y--
                        }
                        // move robot to newX and newY
                        x = newX
                        y = newY
                    }
                }
            }
        }
    }

    fun copy(): Day15Robot {
        return Day15Robot(x, y)
    }
}

class Box(var left: Point, var right: Point) {

    // X is always the same for both points
    fun getX(): Int {
        return left.x
    }

    fun getY(): List<Int> {
        return listOf(left.y, right.y)
    }

    private fun canMove(emptyMatrix: List<List<Char>>, boxes: List<Box>, direction: Facing, path: MutableList<Box> = ArrayList()): Pair<Boolean, List<Box>> {
        when (direction) {
            Facing.NORTH -> {
                if (emptyMatrix[left.x - 1][left.y] == '#' || emptyMatrix[right.x - 1][right.y] == '#') {
                    return Pair(false, path)
                }

                // 0, 1 or 2 boxes above the current box
                val contenders = boxes.filter { box -> box.getY().any { getY().contains(it) } && box.getX() + 1 == left.x }

                return if (contenders.isEmpty() && emptyMatrix[left.x - 1][left.y] == '.' && emptyMatrix[right.x - 1][right.y] == '.') {
                    path.add(this)
                    Pair(true, path)
                } else {
                    val outMap = contenders.map { it.canMove(emptyMatrix, boxes, direction, path) }
                    val outList = outMap.flatMap { it.second }.toMutableList()
                    outList.add(this)
                    Pair(outMap.all { it.first }, outList)
                }
            }
            Facing.SOUTH -> {
                if (emptyMatrix[left.x + 1][left.y] == '#' || emptyMatrix[right.x + 1][right.y] == '#') {
                    return Pair(false, path)
                }

                // 0, 1 or 2 boxes below the current box
                val contenders = boxes.filter { box -> box.getY().any { getY().contains(it) } && box.getX() - 1 == left.x }

                return if (contenders.isEmpty() && emptyMatrix[left.x + 1][left.y] == '.' && emptyMatrix[right.x + 1][right.y] == '.') {
                    path.add(this)
                    Pair(true, path)
                } else {
                    val outMap = contenders.map { it.canMove(emptyMatrix, boxes, direction, path) }
                    val outList = outMap.flatMap { it.second }.toMutableList()
                    outList.add(this)
                    Pair(outMap.all { it.first }, outList)
                }
            }
            else -> throw IllegalArgumentException("Only NORTH and SOUTH directions are supported")
        }
    }

    fun move(emptyMatrix: List<List<Char>>, boxes: MutableList<Box>, direction: Facing): Boolean {
        val (canMove, path) = canMove(emptyMatrix, boxes, direction)
        if (canMove) {
            for (box in path.distinct()) {
                box.left.x += if (direction == Facing.NORTH) -1 else 1
                box.right.x += if (direction == Facing.NORTH) -1 else 1
            }
        }
        return canMove
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Box -> this.left == other.left && this.right == other.right
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(left, right)
    }
}

fun longPow(base: Long, exponent: Long): Long {
    return when (exponent) {
        0L -> 1L
        1L -> base
        else -> {
            base * longPow(base, exponent - 1)
        }
    }
}