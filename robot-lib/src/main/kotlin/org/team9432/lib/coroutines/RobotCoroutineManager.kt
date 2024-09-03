package org.team9432.lib.coroutines

import kotlinx.coroutines.*
import org.littletonrobotics.junction.Logger
import java.lang.Runnable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

object RobotCoroutineManager {
    private const val MILLISECONDS_TO_MICROSECONDS = 1000
    private const val DEBUG_OUTPUT = false

    private data class DelayedContinuation(val continuation: CancellableContinuation<Unit>, val endTimeMicros: Long)

    @OptIn(InternalCoroutinesApi::class)
    private val robotDispatcher: CoroutineDispatcher = object: CoroutineDispatcher(), Delay {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            // Just run the coroutine immediately on this thread for determinism
            // All coroutines spawned during the user code loop will also immediately run here so no extra logic is needed
            block.run()
        }

        override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
            printDebug { "Scheduled delay for $timeMillis milliseconds" }

            // Schedule the continuation to resume after the delay
            val endTimeMicros = Logger.getTimestamp() + (timeMillis * MILLISECONDS_TO_MICROSECONDS)
            newContinuations.add(DelayedContinuation(continuation, endTimeMicros))
        }
    }

    /** The [CoroutineScope] that should be the base of all user-launched coroutines. */
    internal val coroutineScope = CoroutineScope(robotDispatcher)

    // All currently delayed coroutines that need to be resumed at some point
    private val delayedContinuations = mutableListOf<DelayedContinuation>()

    // All continuations that need to be added to the delayedContinuations list next cycle (to avoid ConcurrentModificationException)
    private val newContinuations = mutableListOf<DelayedContinuation>()

    // The current number of delayed coroutines, used primarily for unit testing
    internal val currentDelayedCount get() = delayedContinuations.size + newContinuations.size

    // This function must be called in the robot's periodic loop to update and complete any delayed tasks
    internal fun updateCoroutines() {
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

    private fun printDebug(message: () -> String) {
        if (DEBUG_OUTPUT) println("[Debug] ${message.invoke()}")
    }
}