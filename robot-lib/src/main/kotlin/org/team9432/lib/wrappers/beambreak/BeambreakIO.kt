package org.team9432.lib.wrappers.beambreak

import org.littletonrobotics.junction.LogTable
import org.littletonrobotics.junction.inputs.LoggableInputs

interface BeambreakIO {
    class BeambreakIOInputs: LoggableInputs {
        var isTripped: Boolean = false

        override fun toLog(table: LogTable) {
            table.put("isTripped", isTripped)
        }

        override fun fromLog(table: LogTable) {
            isTripped = table.get("isTripped", isTripped)
        }
    }

    fun updateInputs(inputs: BeambreakIOInputs)
}