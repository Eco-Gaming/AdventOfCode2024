package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile

fun main() {
    val day21 = Day21()
    day21.solve()
}

class Day21 : Puzzle {

    private val codes = ArrayList<String>()

    // fields with a '#' cannot be visited!
    private val numpad = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf('#', '0', 'A')
    )
    private val dirpad = listOf(
        listOf('#', '^', 'A'),
        listOf('<', 'v', '>'),
    )

    private val bfsCache = HashMap<Triple<Char, Char, List<List<Char>>>, List<Pair<Int, List<Char>>>>()
    private val neighborCache = HashMap<Pair<Char, List<List<Char>>>, List<Pair<Char, Char>>>()

    private val dirpadPathCache = HashMap<Pair<List<Char>, Int>, List<Char>>()
    private val dirpadCountCache = HashMap<Pair<List<Char>, Int>, Long>()

    private val getPathCache = HashMap<Pair<Char, Char>, List<Char>>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day21.txt")
        codes.addAll(input.lines())
    }

    override fun solvePartOne(): String {
        var sum = 0L

        // backtrack from end to start
        for (code in codes) {
            val minPathLength = getShortestPathLength(code, 2)
            sum += minPathLength * code.removeSuffix("A").toLong()
        }
        return sum.toString()
    }

    private fun getShortestPathLength(code: String, recursionLevel: Int): Long {
        val possibilitiesRaw = ArrayList<List<List<Char>>>()
        for (i in code.indices) {
            val pathList = bfs(if (i < 1) 'A' else code[i - 1], code[i], numpad)
            possibilitiesRaw.add(pathList.map { it.second.drop(1) + 'A' })
        }
        val path = possibilitiesToPath(possibilitiesRaw)
        val minPath = getDirpadPath(path, recursionLevel)
        return minPath.size.toLong()
    }

    // adapted from Day16
    private fun bfs(start: Char, end: Char, matrix: List<List<Char>>): List<Pair<Int, List<Char>>> {
        if (bfsCache.containsKey(Triple(start, end, matrix))) {
            return bfsCache[Triple(start, end, matrix)]!!
        }

        val res = ArrayList<Pair<Int, List<Pair<Char, Char>>>>()

        val queue = ArrayList<Pair<Int, MutableList<Pair<Char, Char>>>>()
        val seen = HashSet<Char>()
        var bestDistance = Int.MAX_VALUE

        queue.add(Pair(0, mutableListOf(Pair(start, 'O'))))

        while (queue.isNotEmpty()) {
            val (distance, path) = queue.removeFirst()
            val last = path.last().first
            if (distance > bestDistance) continue

            if (last == end) {
                bestDistance = distance
                res.add(Pair(distance, path))
                continue
            }
            seen.add(last)

            getNeighbors(last, matrix)
                .filter { it.first !in seen }
                .forEach {
                    val newPath = ArrayList(path)
                    newPath.add(it)
                    queue.add(Pair(distance + 1, newPath))
                }
        }
        val out = res.map { pair ->
            Pair(pair.first, pair.second.map { it.second })
        }
        bfsCache[Triple(start, end, matrix)] = out
        return out
    }

    // works because all 'nodes' in a matrix are unique
    // Pair<node, direction>
    private fun getNeighbors(char: Char, matrix: List<List<Char>>): List<Pair<Char, Char>> {
        if (neighborCache.containsKey(Pair(char, matrix))) {
            return neighborCache[Pair(char, matrix)]!!
        }

        val neighbors = ArrayList<Pair<Char, Char>>()
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == char) {
                    if (i - 1 >= 0 && matrix[i - 1][j] != '#') neighbors.add(Pair(matrix[i - 1][j], '^'))
                    if (i + 1 < matrix.size && matrix[i + 1][j] != '#') neighbors.add(Pair(matrix[i + 1][j], 'v'))
                    if (j - 1 >= 0 && matrix[i][j - 1] != '#') neighbors.add(Pair(matrix[i][j - 1], '<'))
                    if (j + 1 < matrix[i].size && matrix[i][j + 1] != '#') neighbors.add(Pair(matrix[i][j + 1], '>'))
                    break
                }
            }
        }
        neighborCache[Pair(char, matrix)] = neighbors
        return neighbors
    }

    private fun possibilitiesToPath(possibilities: List<List<List<Char>>>): List<Char> {
        return possibilities.map { column -> // filter out the paths with the least turns
            val newCol = column.map { Pair(getTurns(it), it) }
            val min = newCol.minOf { it.first }
            newCol.filter { it.first == min }
                .map { it.second }
        }.map { column -> // prefer paths starting with '<', then 'v'
            column.firstOrNull { it[0] == '<' } ?: column.firstOrNull { it[0] == 'v' } ?: column[0]
        }.flatten()
    }

    private fun getTurns(sequence: List<Char>): Int {
        var sum = 0
        var prev = sequence[0]
        for (i in 1..<sequence.size) {
            if (sequence[i] != prev) {
                prev = sequence[i]
                sum++
            }
        }
        return sum
    }

    private fun getDirpadPath(path: List<Char>, recursionLevel: Int = 1): List<Char> {
        if (dirpadPathCache.containsKey(Pair(path, recursionLevel))) {
            return dirpadPathCache[Pair(path, recursionLevel)]!!
        }

        val possibilitiesRaw = ArrayList<List<List<Char>>>()
        var mutablePath = path
        for (j in 1..recursionLevel) {
            for (i in mutablePath.indices) {
                val newPathList = bfs(if (i < 1) 'A' else mutablePath[i - 1], mutablePath[i], dirpad)
                possibilitiesRaw.add(newPathList.map { it.second.drop(1) + 'A' })
            }
            mutablePath = possibilitiesToPath(possibilitiesRaw)
            possibilitiesRaw.clear()
        }

        dirpadPathCache[Pair(path, recursionLevel)] = mutablePath
        return mutablePath
    }

    private fun getDirpadPathLength(path: List<Char>, recursionLevel: Int = 1): Long {
        if (recursionLevel == 0) {
            return path.size.toLong()
        }
        if (path == listOf('A')) return 1L
        if (dirpadCountCache.containsKey(Pair(path, recursionLevel))) return dirpadCountCache[Pair(path, recursionLevel)]!!

        val moves = path.fold(mutableListOf(mutableListOf<Char>())) { acc, char ->
            if (char == 'A') {
                acc.add(mutableListOf())
            } else {
                acc.last().add(char)
            }
            acc
        }.dropLastWhile { it.isEmpty() }
        var sum = 0L
        for (move in moves) {
            val out = ArrayList<List<Char>>()
            for (i in 0..move.size) {
                val start = if (i == 0) 'A' else move[i - 1]
                val end = if (i == move.size) 'A' else move[i]
                val path2 = getPath(start, end, dirpad)
                out.add(path2)
            }
            sum += getDirpadPathLength(out.flatten(), recursionLevel - 1)
        }
        dirpadCountCache[Pair(path, recursionLevel)] = sum
        return sum
    }

    private fun getPath(start: Char, end: Char, matrix: List<List<Char>>): List<Char> {
        if (getPathCache.containsKey(Pair(start, end))) return getPathCache[Pair(start, end)]!!

        val possibilitiesRaw = ArrayList<List<List<Char>>>()
        val pathList = bfs(start, end, matrix)
        possibilitiesRaw.add(pathList.map { it.second.drop(1) + 'A' })
        val path = possibilitiesToPath(possibilitiesRaw)

        getPathCache[Pair(start, end)] = path
        return path
    }

    override fun solvePartTwo(): String {
        var sum = 0L
        for (code in codes) {
            val possibilitiesRaw = ArrayList<List<List<Char>>>()
            for (i in code.indices) {
                val pathList = bfs(if (i < 1) 'A' else code[i - 1], code[i], numpad)
                possibilitiesRaw.add(pathList.map { it.second.drop(1) + 'A' })
            }
            val path = possibilitiesToPath(possibilitiesRaw)
            val newSum = getDirpadPathLength(path, 25) * code.removeSuffix("A").toLong()
            sum += newSum

            // for debugging:
            // println("code: $code, sum: $newSum")
        }

        return sum.toString()
    }
}