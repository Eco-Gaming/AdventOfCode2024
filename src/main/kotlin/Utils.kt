package me.eco_gaming

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

    fun fullEquals(other: Any?): Boolean {
        return when (other) {
            is GuardPosition -> this.x == other.x && this.y == other.y && this.facing == other.facing
            else -> false
        }
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is GuardPosition -> this.x == other.x && this.y == other.y
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y)
    }
}

enum class Operator {
    PLUS, TIMES, CONCAT
}