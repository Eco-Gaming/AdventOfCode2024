package me.eco_gaming

interface Puzzle {

    fun readFile()

    fun solvePartOne(): String
    fun solvePartTwo(): String

    fun solve() {
        readFile()
        println("Part 1: " + solvePartOne())
        println("Part 2: " + solvePartTwo())
    }
}