package me.eco_gaming.utils

import kotlin.math.roundToLong

class ClawMachine(
    private val buttonAx: Double,
    private val buttonAy: Double,
    private val buttonBx: Double,
    private val buttonBy: Double,
    private val prizeX: Double,
    private val prizeY: Double,
    private val maxStepsA: Double = 100.0,
    private val maxStepsB: Double = 100.0) {

    fun solveEquation(offset: Long = 0, limitRange: Boolean = true): Pair<Long, Long>? {
        val solutionA = ((prizeX+offset)*buttonBy - (prizeY+offset)*buttonBx) / (buttonAx*buttonBy - buttonAy*buttonBx)
        val solutionB = ((prizeY+offset)*buttonAx - (prizeX+offset)*buttonAy) / (buttonAx*buttonBy - buttonAy*buttonBx)
        if (!limitRange || (solutionA in 0.0..maxStepsA && solutionB in 0.0..maxStepsB)) {
            val aInt = solutionA.roundToLong()
            val bInt = solutionB.roundToLong()
            if (aInt*buttonAx + bInt*buttonBx == prizeX+offset && aInt*buttonAy + bInt*buttonBy == prizeY+offset) {
                return Pair(aInt, bInt)
            }
        }
        return null
    }
}