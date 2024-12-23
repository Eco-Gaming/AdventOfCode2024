package me.eco_gaming.puzzles

import me.eco_gaming.Puzzle
import me.eco_gaming.readInputFromFile
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

fun main() {
    val day23 = Day23()
    day23.solve()
}

class Day23 : Puzzle {

    private val connections = ArrayList<Pair<String, String>>()

    private val tripleCache = HashSet<Triple<String, String, String>>()

    override fun readFile() {
        val input = readInputFromFile("src/main/resources/day23.txt")
        for (line in input.lines()) {
            val parts = line.split("-")
            connections.add(Pair(parts[0], parts[1]))
        }
    }

    override fun solvePartOne(): String {
        val triples = getConnectionTriples(connections)
        tripleCache.addAll(triples) // for part 2
        val res = triples.count { it.first.startsWith("t") || it.second.startsWith("t") || it.third.startsWith("t") }
        return res.toString()
    }

    private fun getConnectionTriples(connectionList: List<Pair<String, String>>): List<Triple<String, String, String>> {
        val triples = ArrayList<Triple<String, String, String>>()
        for (connection in connectionList) {
            val connectionListTemp = getAllConnections(connection.first, connectionList)
            for (i in connectionListTemp.indices) {
                for (j in i + 1 until connectionListTemp.size) {
                    val con1 = connectionListTemp[i]
                    val con2 = connectionListTemp[j]
                    if (Pair(con1, con2) in connectionList || Pair(con2, con1) in connectionList) {
                        val tripleList = mutableListOf(connection.first, con1, con2)
                        tripleList.sort()
                        triples.add(Triple(tripleList[0], tripleList[1], tripleList[2]))
                    }
                }
            }
        }
        return triples.distinct()
    }

    private fun getAllConnections(connection: String, connectionList: List<Pair<String, String>>): List<String> {
        val res = HashSet<String>()
        for (entry in connectionList) {
            when (connection) {
                entry.first -> res.add(entry.second)
                entry.second -> res.add(entry.first)
            }
        }
        return res.toList()
    }

    override fun solvePartTwo(): String {
        val pools = getPools(connections)
        println(pools)
        val res = pools.maxByOrNull { it.size }!!
        return res.joinToString(",")
    }

    // will get all pools with a minimum size of 4
    // this method makes some naive assumptions, but works for the example and my puzzle input
    private fun getPools(connectionList: List<Pair<String, String>>): List<List<String>> {
        val pools = ArrayList<List<String>>()
        val triples = tripleCache
        val nodes = connectionList.map { listOf(it.first, it.second) }.flatten().toHashSet()

        for (node in nodes) {
            val contenders = triples.filter { it.first == node || it.second == node || it.third == node }
            if (contenders.size < 2) continue // expect the minimum result to be 4, for efficiency

            // if *all* nodes within *all* triples are interconnected, add to pool
            // this may not work for all inputs!
            val contenderList = contenders.map { listOf(it.first, it.second, it.third) }.flatten().distinct()
            if (checkAllConnections(contenderList, connectionList)) {
                pools.add(contenderList.sorted())
            }
        }

        return pools.distinct()
    }

    private fun checkAllConnections(pool: List<String>, connectionList: List<Pair<String, String>>): Boolean {
        for (i in pool.indices) {
            for (j in i + 1 until pool.size) {
                val con1 = pool[i]
                val con2 = pool[j]
                if (Pair(con1, con2) !in connectionList && Pair(con2, con1) !in connectionList) {
                    return false
                }
            }
        }
        return true
    }
}