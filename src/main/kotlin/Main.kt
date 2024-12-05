package me.eco_gaming

import me.eco_gaming.puzzles.*

fun main() {
    val puzzles = listOf(
        Day01(),
        Day02(),
        Day03(),
        Day04(),
        Day05(),
    )

    for (puzzle in puzzles) {
        println(puzzle.javaClass.simpleName + ":")
        puzzle.solve()
        println()
    }
}