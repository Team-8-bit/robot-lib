package org.team9432.lib.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

suspend fun await(condition: () -> Boolean, period: Duration = 20.milliseconds) {
    while (!condition()) delay(period)
}

suspend fun robotPeriodic(function: () -> Boolean) {
    suspendCancellableCoroutine { cont ->
        val periodic = CoroutineRobot.startPeriodic {
            val isFinished = function.invoke()
            if (isFinished) {
                stopPeriodic()
                cont.resume(Unit)
            }
        }

        cont.invokeOnCancellation {
            periodic.stopPeriodic()
        }
    }
}