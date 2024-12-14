package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.Robot
import me.eco_gaming.readInputFromFile
import java.awt.Point

fun main() {
    val day14 = Day14()
    day14.solve()
}

class Day14 : Puzzle {

    private val dimensions = Point(100, 102) // largest point which is still in bounds, has to be set manually
    private val robotList = ArrayList<Robot>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day14.txt")
        for (line in input.lines()) {
            val pos = line.split(" ")[0].split("=")[1].split(",")
            val vector = line.split(" ")[1].split("=")[1].split(",")
            robotList.add(Robot(pos[0].toInt(), pos[1].toInt(), vector[0].toInt(), vector[1].toInt()))
        }
    }

    override fun solvePartOne(): String {
        val map = getRobotMap(100)
        val quadrants = calculateQuadrants(map)
        return quadrants.fold(1, { acc, i -> acc * i }).toString()
    }

    override fun solvePartTwo(): String {
        TODO("Not yet implemented")
    }

    private fun getRobotMap(steps: Int, list: List<Robot> = robotList, mapDimensions: Point = dimensions): Array<Array<Int>> {
        val listCopy = list.toList()
        val map = Array(mapDimensions.x + 1) { Array(mapDimensions.y + 1) { 0 } }
        for (robot in listCopy) {
            robot.step(mapDimensions.x, mapDimensions.y, steps)
            map[robot.x][robot.y]++
        }
        return map
    }

    private fun calculateQuadrants(map: Array<Array<Int>>): List<Int> {
        val list = mutableListOf(0, 0, 0, 0)
        for (i in map.indices) {
            for (j in map[i].indices) {
                val quadrant = getQuadrant(map, i, j)
                if (map[i][j] > 0 && quadrant != -1) {
                    list[quadrant] += map[i][j]
                }
            }
        }
        return list
    }

    // 0 1
    // 3 2
    private fun getQuadrant(map: Array<Array<Int>>, x: Int, y: Int): Int {
        val xMid = map.size / 2
        val yMid = map[0].size / 2
        return when {
            x < xMid && y < yMid -> 0
            x > xMid && y < yMid -> 1
            x > xMid && y > yMid -> 2
            x < xMid && y > yMid -> 3
            else -> -1
        }
    }
}