package org.team9432.lib.simulation.math

import org.team9432.lib.GIT_SHA
import java.util.*
import kotlin.math.*

object MapleCommonMath {
    /**
     * random object that generates random variables
     * the seed is the hash of GIT_SHA
     * this way when you do log-replay even the generated random numbers are the same
     */
    private val random: Random = Random(GIT_SHA.hashCode().toLong())

    /**
     * using the random number generator of a fixed seed, generate the next random normal variable
     *
     * @param mean   the center of the distribution
     * @param stdDev the standard deviation of the distribution
     * @return the next random variable x from the distribution
     */
    fun generateRandomNormal(mean: Double, stdDev: Double): Double {
        val u1: Double = random.nextDouble()
        val u2: Double = random.nextDouble()
        // Box–Muller transform https://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform
        val z0: Double = sqrt(-2.0 * ln(u1)) * cos(2.0 * Math.PI * u2)
        return z0 * stdDev + mean
    }

    fun constrainMagnitude(value: Double, maxMagnitude: Double): Double {
        return min(
            abs(value),
            abs(maxMagnitude)
        ).withSign(
            value
        )
    }

}
