package org.team9432.lib.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import org.team9432.lib.RobotPeriodicManager
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

suspend fun await(period: Duration = 20.milliseconds, condition: () -> Boolean) {
    while (!condition()) delay(period)
}

suspend fun robotPeriodic(isFinished: () -> Boolean, function: () -> Unit) {
    suspendCancellableCoroutine { cont ->
        val periodic = RobotPeriodicManager.startPeriodic {
            function.invoke()

            if (isFinished.invoke()) {
                stopPeriodic()
                cont.resume(Unit)
            }
        }

        cont.invokeOnCancellation {
            periodic.stopPeriodic()
        }
    }
}