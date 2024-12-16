package me.eco_gaming.puzzles

import me.eco_gaming.Facing
import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import java.awt.Point

fun main() {
    val day16 = Day16()
    day16.solve()
}

class Day16 : Puzzle {

    private val matrix = ArrayList<List<Char>>()
    private val start = Point(-1, -1)
    private val end = Point(-1, -1)

    private val resultCache = ArrayList<Pair<Int, List<Pair<Point, Facing>>>>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day16.txt")
        for ((i, line) in input.lines().withIndex()) {
            val row = ArrayList<Char>()
            for ((j, char) in line.withIndex()) {
                when (char) {
                    'S' -> {
                        start.x = i
                        start.y = j
                        row.add('.')
                    }
                    'E' -> {
                        end.x = i
                        end.y = j
                        row.add('.')
                    }
                    else -> {
                        row.add(char)
                    }
                }
            }
            matrix.add(row)
        }
    }

    override fun solvePartOne(): String {
        // List<distance, path>
        val res = bfs(Pair(start, Facing.EAST), end)
        val shortestPath = res.minBy { it.first }.first
        resultCache.addAll(res.filter { it.first == shortestPath })
        return shortestPath.toString()
    }

    override fun solvePartTwo(): String {
        // List<distance, path>
        val res = resultCache
        val spots = res.flatMap { it.second }.distinctBy { Pair(it.first.x, it.first.y) }
        return spots.size.toString()
    }

    private fun bfs(start: Pair<Point, Facing>, end: Point): List<Pair<Int, List<Pair<Point, Facing>>>> {
        val res = ArrayList<Pair<Int, List<Pair<Point, Facing>>>>()

        val queue = ArrayList<Pair<Int, MutableList<Pair<Point, Facing>>>>()
        val seen = HashSet<Pair<Point, Facing>>()
        var bestDistance = Int.MAX_VALUE

        queue.add(Pair(0, mutableListOf(start)))

        while (queue.isNotEmpty()) {
            val (distance, path) = queue.removeFirst()
            val last = path.last()
            if (distance > bestDistance) continue

            if (last.first == end) {
                bestDistance = distance
                res.add(Pair(distance, path))
                continue
            }
            seen.add(last)

            // check forward
            val nextForward = when (last.second) {
                Facing.NORTH -> Pair(Point(last.first.x - 1, last.first.y), Facing.NORTH)
                Facing.EAST -> Pair(Point(last.first.x, last.first.y + 1), Facing.EAST)
                Facing.SOUTH -> Pair(Point(last.first.x + 1, last.first.y), Facing.SOUTH)
                Facing.WEST -> Pair(Point(last.first.x, last.first.y - 1), Facing.WEST)
            }
            if (matrix[nextForward.first.x][nextForward.first.y] == '.' && nextForward !in seen) {
                val newPath = path.toMutableList()
                newPath.add(nextForward)
                queue.add(Pair(distance + 1, newPath))
            }

            // check left
            val nextLeft = when (last.second) {
                Facing.NORTH -> Pair(Point(last.first.x, last.first.y - 1), Facing.WEST)
                Facing.EAST -> Pair(Point(last.first.x - 1, last.first.y), Facing.NORTH)
                Facing.SOUTH -> Pair(Point(last.first.x, last.first.y + 1), Facing.EAST)
                Facing.WEST -> Pair(Point(last.first.x + 1, last.first.y), Facing.SOUTH)
            }
            if (matrix[nextLeft.first.x][nextLeft.first.y] == '.' && nextLeft !in seen) {
                val newPath = path.toMutableList()
                newPath.add(nextLeft)
                queue.add(Pair(distance + 1001, newPath))
            }

            // check right
            val nextRight = when (last.second) {
                Facing.NORTH -> Pair(Point(last.first.x, last.first.y + 1), Facing.EAST)
                Facing.EAST -> Pair(Point(last.first.x + 1, last.first.y), Facing.SOUTH)
                Facing.SOUTH -> Pair(Point(last.first.x, last.first.y - 1), Facing.WEST)
                Facing.WEST -> Pair(Point(last.first.x - 1, last.first.y), Facing.NORTH)
            }
            if (matrix[nextRight.first.x][nextRight.first.y] == '.' && nextRight !in seen) {
                val newPath = path.toMutableList()
                newPath.add(nextRight)
                queue.add(Pair(distance + 1001, newPath))
            }

            // breadth first search expects the queue to be sorted by distance
            // this has to be done manually here, as turns are 'equal' to 1000 steps
            queue.sortBy { it.first }
        }
        return res
    }
}