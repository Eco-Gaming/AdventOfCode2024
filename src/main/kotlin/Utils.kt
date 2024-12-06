package me.eco_gaming

import java.io.BufferedReader
import java.io.File

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

class GuardPosition(var x: Int, var y: Int, var facing: Facing) {}