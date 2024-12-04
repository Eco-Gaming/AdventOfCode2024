package me.eco_gaming

import me.eco_gaming.puzzles.Day01
import me.eco_gaming.puzzles.Day02
import me.eco_gaming.puzzles.Day03
import me.eco_gaming.puzzles.Day04

fun main() {
    val puzzles = listOf(
        Day01(),
        Day02(),
        Day03(),
        Day04(),
    )

    for (puzzle in puzzles) {
        println(puzzle.javaClass.simpleName + ":")
        puzzle.solve()
        println()
    }
}