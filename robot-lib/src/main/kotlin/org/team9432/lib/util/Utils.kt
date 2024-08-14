package org.team9432.lib.util

import edu.wpi.first.wpilibj.DriverStation
import kotlinx.coroutines.delay
import org.team9432.lib.LibraryState
import kotlin.time.Duration

/** Returns either [blue] or [red] depending on the alliance color. */
fun <T> allianceSwitch(blue: T, red: T): T = if (LibraryState.alliance == DriverStation.Alliance.Blue) blue else red

/** Calls the given block when the robot is running in simulation. */
fun whenSimulated(block: () -> Unit) {
    if (LibraryState.isSimulated) {
        block.invoke()
    }
}

/** Calls the given block when the robot is running in simulation. */
suspend fun whenSimulatedSuspend(block: suspend () -> Unit) {
    if (LibraryState.isSimulated) {
        block.invoke()
    }
}

/** Delays for the given duration when the robot is running in simulation. */
suspend fun simDelay(duration: Duration) {
    if (LibraryState.isSimulated) {
        delay(duration)
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