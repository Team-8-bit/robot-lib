package org.team9432.lib.coroutines

import kotlinx.coroutines.*
import org.littletonrobotics.junction.LoggedRobot
import org.littletonrobotics.junction.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

private const val MILLISECONDS_TO_MICROSECONDS = 1000

open class LoggedCoroutineRobot(private val coroutineDebugOutput: Boolean = false): LoggedRobot(PERIOD) {
    @OptIn(InternalCoroutinesApi::class)
    private val robotDispatcher: CoroutineDispatcher = object: CoroutineDispatcher(), Delay {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            block.run() // Directly run on the same thread for simplicity
        }

        override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
            printDebug { "Scheduled delay for $timeMillis milliseconds" }

            // Schedule the continuation to resume after the delay
            val endTimeMicros = Logger.getTimestamp() + (timeMillis * MILLISECONDS_TO_MICROSECONDS)
            newContinuations.add(DelayedContinuation(continuation, endTimeMicros))
        }
    }

    val RobotScope = CoroutineScope(robotDispatcher)

    private val dispatchedJobs = mutableListOf<Runnable>()

    private data class DelayedContinuation(val continuation: CancellableContinuation<Unit>, val endTimeMicros: Long)

    private val delayedContinuations = mutableListOf<DelayedContinuation>()
    private val newContinuations = mutableListOf<DelayedContinuation>()

    internal val currentDelayedCount get() = delayedContinuations.size + newContinuations.size

    override fun robotPeriodic() {
        val jobsToRun = dispatchedJobs.toList()
        dispatchedJobs.clear()
        jobsToRun.forEach { it.run() }

        delayedContinuations.addAll(newContinuations)
        newContinuations.clear()
        val iterator = delayedContinuations.listIterator()
        val currentTimestamp = Logger.getTimestamp()
        for ((continuation, endTimeMicros) in iterator) {
            if (currentTimestamp >= endTimeMicros) {
                continuation.resume(Unit)
                iterator.remove()
            }
        }
    }

    fun printDebug(message: () -> String) {
        if (coroutineDebugOutput) println("[Debug] ${message.invoke()}")
    }

    companion object {
        const val PERIOD = 0.02
    }
}