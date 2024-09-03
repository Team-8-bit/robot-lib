package org.team9432.lib

import edu.wpi.first.wpilibj.DigitalInput
import kotlinx.coroutines.delay
import org.team9432.lib.coroutines.await
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Beambreak(dioChannel: Int) {
    private val digitalInput = DigitalInput(dioChannel)
    private var simStateTripped: Boolean = false

    fun isTripped() = if (Library.isSimulated) simStateTripped else !digitalInput.get()
    fun isClear() = if (Library.isSimulated) !simStateTripped else digitalInput.get()

    suspend fun awaitTripped(period: Duration = 20.milliseconds, simDelay: Duration = Duration.INFINITE) {
        if (Library.isSimulated) {
            if (!simStateTripped) {
                delay(simDelay)
                simStateTripped = true
            }
        } else {
            await(period, ::isTripped)
        }
    }

    suspend fun awaitClear(period: Duration = 20.milliseconds, simDelay: Duration = Duration.INFINITE) {
        if (Library.isSimulated) {
            if (simStateTripped) {
                delay(simDelay)
                simStateTripped = false
            }
        } else {
            await(period, ::isClear)
        }
    }

    fun setSimTripped() {
        simStateTripped = true
    }

    fun setSimClear() {
        simStateTripped = false
    }
}