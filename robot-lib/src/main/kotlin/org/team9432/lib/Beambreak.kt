package org.team9432.lib

import edu.wpi.first.wpilibj.DigitalInput
import kotlinx.coroutines.delay
import org.team9432.lib.coroutines.await
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Beambreak(dioChannel: Int) {
    private val digitalInput = DigitalInput(dioChannel)
    private var simStateTripped: Boolean = false

    fun isTripped() = if (LibraryState.isSimulation) simStateTripped else !digitalInput.get()
    fun isClear() = if (LibraryState.isSimulation) !simStateTripped else digitalInput.get()

    suspend fun awaitTripped(period: Duration = 20.milliseconds, simDelay: Duration = Duration.INFINITE) {
        if (LibraryState.isSimulation) {
            delay(simDelay)
            simStateTripped = true
        } else {
            await(period, ::isTripped)
        }
    }

    suspend fun awaitClear(period: Duration = 20.milliseconds, simDelay: Duration = Duration.INFINITE) {
        if (LibraryState.isSimulation) {
            delay(simDelay)
            simStateTripped = false
        } else {
            await(period, ::isClear)
        }
    }
}