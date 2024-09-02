package org.team9432.lib.coroutines

import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.Timer
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.InternalCoroutinesApi
import org.team9432.lib.RobotPeriodicManager
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

@OptIn(InternalCoroutinesApi::class)
object TeamDispatcher: CoroutineDispatcher(), Delay {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        block.run() // Directly run on the same thread for simplicity
    }

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        println("Custom delay for $timeMillis milliseconds")
        // Schedule the continuation to resume after the delay

        val startTime = Timer.getFPGATimestamp()

        RobotPeriodicManager.startPeriodic {
            val currentTime = Timer.getFPGATimestamp()
            val elapsedTime = currentTime - startTime
            val elapsedTimeMillis = Units.secondsToMilliseconds(elapsedTime)

            if (elapsedTimeMillis >= timeMillis) {
                continuation.resume(Unit)
                stopPeriodic()
            }
        }
    }
}
