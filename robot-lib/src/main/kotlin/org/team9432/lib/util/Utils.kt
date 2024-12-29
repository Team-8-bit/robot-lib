package org.team9432.lib.util

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