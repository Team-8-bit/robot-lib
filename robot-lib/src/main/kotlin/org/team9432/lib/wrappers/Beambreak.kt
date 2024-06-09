package org.team9432.lib.wrappers

import edu.wpi.first.wpilibj.DigitalInput
import org.team9432.lib.coroutines.await
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Beambreak(dioChannel: Int) {
    private val digitalInput = DigitalInput(dioChannel)

    fun isTripped() = !digitalInput.get()
    fun isUnbroken() = digitalInput.get()

    suspend fun awaitTripped(period: Duration = 20.milliseconds) = await(::isTripped, period)
    suspend fun awaitUnbroken(period: Duration = 20.milliseconds) = await(::isUnbroken, period)
}