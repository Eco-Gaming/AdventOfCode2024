package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import java.awt.Point

fun main() {
    val day08 = Day08()
    day08.solve()
}

infix fun Point.inBounds(bounds: Point): Boolean {
    return (this.x in 0..bounds.x) && (this.y in 0..bounds.y)
}

class Day08 : Puzzle {

    private val antennas = HashMap<Char, MutableList<Point>>()
    private val dimensions = Point(0, 0) // max allowed coordinates

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day08.txt")
        for ((i, line) in input.lines().withIndex()) {
            for ((j, char) in line.withIndex()) {
                if (char == '.' || char == '#') continue // empty spot or antinode
                if (antennas.containsKey(char)) {
                    antennas[char]?.add(Point(i, j))
                } else {
                    antennas[char] = mutableListOf(Point(i, j))
                }
            }
        }
        dimensions.x = input.lines().size - 1
        dimensions.y = input.lines()[0].length - 1
    }

    override fun solvePartOne(): String {
        val antinodes = getAntinodes()
        val distinctAntinodes = antinodes.values.flatten().distinct()
        return distinctAntinodes.size.toString()
    }

    override fun solvePartTwo(): String {
        val antinodes = getAntinodes(partTwo = true)
        val distinctAntinodes = antinodes.values.flatten().distinct()
        return distinctAntinodes.size.toString()
    }

    private fun getAntinodes(antennaMap: Map<Char, List<Point>> = antennas, endPoint: Point = dimensions, partTwo: Boolean = false): Map<Char, List<Point>> {
        val antinodes = HashMap<Char, MutableList<Point>>()
        for (antenna in antennaMap) {
            // get all possible Point pairs
            val pointPairs = ArrayList<Pair<Point, Point>>()
            for ((i, point) in antenna.value.withIndex()) {
                for (j in i + 1..<antenna.value.size) {
                    pointPairs.add(Pair(point, antenna.value[j]))
                }
            }
            // for every pair, get the antinode positions
            getAntinodePositions(pointPairs, endPoint, antinodes, antenna, partTwo)
        }
        return antinodes
    }

    private fun getAntinodePositions(
        pointPairs: ArrayList<Pair<Point, Point>>,
        endPoint: Point,
        antinodes: HashMap<Char, MutableList<Point>>,
        antenna: Map.Entry<Char, List<Point>>,
        partTwo: Boolean
    ) {
        // initialize the map
        antinodes[antenna.key] = ArrayList()

        for (pair in pointPairs) {
            val dx = pair.first.x - pair.second.x
            val dy = pair.first.y - pair.second.y

            val nextAntinodePos = Point(pair.second) // second to include the first point in antinode list
            nextAntinodePos.translate(dx, dy)
            if (!partTwo) {
                // skip the tower
                nextAntinodePos.translate(dx, dy)
                if (nextAntinodePos.inBounds(endPoint)) antinodes[antenna.key]?.add(nextAntinodePos)
            } else {
                while (nextAntinodePos.inBounds(endPoint)) {
                    antinodes[antenna.key]?.add(Point(nextAntinodePos))
                    nextAntinodePos.translate(dx, dy)
                }
            }

            val nextAntinodeNeg = Point(pair.first) // first to include the second point in antinode list
            nextAntinodeNeg.translate(-dx, -dy)
            if (!partTwo) {
                // skip the tower
                nextAntinodeNeg.translate(-dx, -dy)
                if (nextAntinodeNeg.inBounds(endPoint)) antinodes[antenna.key]?.add(nextAntinodeNeg)
            } else {
                while (nextAntinodeNeg.inBounds(endPoint)) {
                    antinodes[antenna.key]?.add(Point(nextAntinodeNeg))
                    nextAntinodeNeg.translate(-dx, -dy)
                }
            }
        }
    }
}