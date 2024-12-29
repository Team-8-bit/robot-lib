package org.team9432.lib.util

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer.delay
import org.team9432.lib.Library
import kotlin.time.Duration

/** Returns either [blue] or [red] depending on the alliance color. */
fun <T> allianceSwitch(blue: T, red: T): T = if (Library.alliance == DriverStation.Alliance.Blue) blue else red

/** Returns either [real] or [sim] depending on if the robot is simulated or not. */
fun <T> simSwitch(real: T, sim: T): T = if (Library.isSimulated) sim else real

/** Returns either [real] or [sim] depending on if the robot is simulated or not. */
fun <T> simSwitch(real: () -> T, sim: () -> T): T = if (Library.isSimulated) sim.invoke() else real.invoke()

/** Calls the given block when the robot is running in simulation. */
fun whenSimulated(block: () -> Unit) {
    if (Library.isSimulated) {
        block.invoke()
    }
}

/** Prints this and returns it. Useful for inserting into call chains while debugging. */
fun <T> T.printinln(): T {
    println(this.toString())
    return this
}

/** Passes this into [text], prints the output, and returns it. Useful for inserting into call chains while debugging. */
inline fun <T> T.printinln(text: (T) -> Any?): T {
    println(text(this))
    return this
}


private const val EPSILON = 1e-12

fun epsilonEquals(a: Double, b: Double, epsilon: Double) = (a - epsilon <= b) && (a + epsilon >= b)

fun epsilonEquals(a: Double, b: Double) = epsilonEquals(a, b, EPSILON)

fun epsilonEquals(a: Int, b: Int, epsilon: Int) = (a - epsilon <= b) && (a + epsilon >= b)