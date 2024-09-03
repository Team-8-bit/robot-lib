package org.team9432.lib.coroutines

import kotlinx.coroutines.*
import org.littletonrobotics.junction.LoggedRobot
import org.littletonrobotics.junction.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

object LoggedCoroutineRobot: LoggedRobot() {
    val RobotScope = CoroutineScope(TeamDispatcher)

    private val dispatchedJobs = mutableListOf<Runnable>()

    private data class DelayedContinuation(val continuation: CancellableContinuation<Unit>, val endTimeMicros: Long)

    private val delayedContinuations = mutableListOf<DelayedContinuation>()

    private const val MILLISECONDS_TO_MICROSECONDS = 1000

    @OptIn(InternalCoroutinesApi::class)
    object TeamDispatcher: CoroutineDispatcher(), Delay {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            block.run() // Directly run on the same thread for simplicity
        }

        override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
            println("Custom delay for $timeMillis milliseconds")

            // Schedule the continuation to resume after the delay
            val endTimeMicros = Logger.getTimestamp() + (timeMillis * MILLISECONDS_TO_MICROSECONDS)
            delayedContinuations.add(DelayedContinuation(continuation, endTimeMicros))
        }
    }

    override fun robotPeriodic() {
        val jobsToRun = dispatchedJobs.toList()
        dispatchedJobs.clear()
        jobsToRun.forEach { it.run() }

        val iterator = delayedContinuations.listIterator()
        val currentTimestamp = Logger.getTimestamp()
        for ((continuation, endTimeMicros) in iterator) {
            if (currentTimestamp >= endTimeMicros) {
                continuation.resume(Unit)
                iterator.remove()
            }
        }
    }
}