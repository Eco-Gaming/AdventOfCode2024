package me.eco_gaming

import me.eco_gaming.puzzles.Day03

fun main() {
    println("Hello World!")
    val puzzles = listOf(
        Day03(),
    )

    for (puzzle in puzzles) {
        println(puzzle.javaClass.simpleName + ":")
        puzzle.solve()
        println()
    }
}