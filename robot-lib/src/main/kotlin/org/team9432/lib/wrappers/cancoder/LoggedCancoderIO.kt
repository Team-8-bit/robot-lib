package org.team9432.lib.wrappers.cancoder

import edu.wpi.first.math.geometry.Rotation2d

interface LoggedCancoderIO {
    open class CancoderIOInputs(private val additionalQualifier: String = "") {
        var position = Rotation2d()
    }

    fun updateInputs(inputs: CancoderIOInputs) {}
}