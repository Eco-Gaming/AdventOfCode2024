package me.eco_gaming

import java.io.BufferedReader
import java.io.File

fun readInputFromFile(filePath: String): String {
    val bufferedReader: BufferedReader = File(filePath).bufferedReader()
    return bufferedReader.use { it.readText() }
}