package me.eco_gaming

import me.eco_gaming.puzzles.*

fun main() {
    val puzzles = listOf(
        Day01(),
        Day02(),
        Day03(),
        Day04(),
        Day05(),
        Day06(),
        Day07(),
        Day08(),
        Day09(),
        Day10(),
        Day11(),
        Day12(),
        Day13(),
        Day14(),
        Day15(),
        Day16(),
        Day17(),
        Day18(),
        Day19(),
        Day20(),
        Day21(),
        Day22(),
        Day23(),
        Day24(),
        Day25(),
    )

    for (puzzle in puzzles) {
        println(puzzle.javaClass.simpleName + ":")
        puzzle.solve()
        println()
    }
}