package org.team9432.lib.util

import edu.wpi.first.wpilibj.DriverStation
import org.team9432.lib.LibraryState

/** Returns either [blue] or [red] depending on the alliance color. */
fun <T> allianceSwitch(blue: T, red: T): T = if (LibraryState.alliance == DriverStation.Alliance.Blue) blue else red

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