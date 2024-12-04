package me.eco_gaming

import me.eco_gaming.puzzles.Day01
import me.eco_gaming.puzzles.Day03

fun main() {
    val puzzles = listOf(
        Day01(),
        Day03(),
    )

    for (puzzle in puzzles) {
        println(puzzle.javaClass.simpleName + ":")
        puzzle.solve()
        println()
    }
}