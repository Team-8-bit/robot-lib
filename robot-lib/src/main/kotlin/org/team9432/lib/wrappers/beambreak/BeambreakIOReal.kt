package org.team9432.lib.wrappers.beambreak

import edu.wpi.first.wpilibj.DigitalInput

class BeambreakIOReal(dio: Int): BeambreakIO {
    private val digitalInput = DigitalInput(dio)

    override fun updateInputs(inputs: BeambreakIO.BeambreakIOInputs) {
        inputs.isTripped = !digitalInput.get()
    }
}