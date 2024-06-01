package org.team9432.lib.coroutines

import edu.wpi.first.hal.HALUtil
import edu.wpi.first.hal.NotifierJNI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration

class CoroutineNotifier(period: Duration): AutoCloseable {
    private val notifier = NotifierJNI.initializeNotifier()

    private val period = period.inWholeMicroseconds

    private var nextCycle = 0L

    suspend fun suspendTime() {
        if (closed) throw IllegalStateException("suspendTime() called on a disposed notifier! Check usages of close() for the relevant instance")

        val currentTime = HALUtil.getFPGATime()
        if (nextCycle < currentTime) {
            // Loop overrun, start next cycle immediately
            nextCycle = currentTime
        } else {
            NotifierJNI.updateNotifierAlarm(notifier, nextCycle)
            withContext(Dispatchers.IO) {
                NotifierJNI.waitForNotifierAlarm(notifier)
            }
        }
        nextCycle += period
    }


    private var closed = false

    /**
     * Disposes of the internal notifier handle, and **invalidates** the associated [CoroutineNotifier]
     *
     * This should be the last function called before the instance goes out of scope for garbage collection.
     * Alarm resetting should not be done if the notifier has been closed.
     */
    override fun close() {
        closed = true
        NotifierJNI.stopNotifier(notifier)
        NotifierJNI.cleanNotifier(notifier)
    }
}
