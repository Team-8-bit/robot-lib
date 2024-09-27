package org.team9432.lib.wrappers.beambreak

class BeambreakIOSim: BeambreakIO {
    private var internalStateTripped = false

    override fun updateInputs(inputs: BeambreakIO.BeambreakIOInputs) {
        inputs.isTripped = internalStateTripped
    }

    fun setTripped() {
        internalStateTripped = true
    }

    fun setClear() {
        internalStateTripped = false
    }
}