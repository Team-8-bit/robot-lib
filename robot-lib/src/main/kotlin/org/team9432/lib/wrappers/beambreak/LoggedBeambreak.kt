package org.team9432.lib.wrappers.beambreak

import kotlinx.coroutines.delay
import org.littletonrobotics.junction.Logger
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.coroutines.await
import org.team9432.lib.util.simSwitch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class LoggedBeambreak(dioChannel: Int, private val key: String) {
    private val io = simSwitch(sim = { BeambreakIOSim() }, real = { BeambreakIOReal(dioChannel) })
    private val inputs = BeambreakIO.BeambreakIOInputs()

    init {
        RobotPeriodicManager.startPeriodic { periodic() }
    }

    fun isTripped() = inputs.isTripped
    fun isClear() = !inputs.isTripped

    suspend fun awaitTripped(period: Duration = 20.milliseconds, simDelay: Duration = Duration.INFINITE) {
        if (isTripped()) return

        if (io is BeambreakIOSim) {
            delay(simDelay)
            io.setTripped()
        } else {
            await(period, ::isTripped)
        }
    }

    suspend fun awaitClear(period: Duration = 20.milliseconds, simDelay: Duration = Duration.INFINITE) {
        if (isClear()) return

        if (io is BeambreakIOSim) {
            delay(simDelay)
            io.setClear()
        } else {
            await(period, ::isClear)
        }
    }

    fun setSimTripped() {
        if (io is BeambreakIOSim) io.setTripped()
    }

    fun setSimClear() {
        if (io is BeambreakIOSim) io.setClear()
    }

    private fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs(key, inputs)
    }
}