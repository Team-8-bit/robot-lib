package org.team9432.lib.wrappers.neo

import edu.wpi.first.math.geometry.Rotation2d

interface LoggedNeoIO {
    open class NEOIOInputs(private val additionalQualifier: String = "")

    fun getAngle(): Rotation2d = Rotation2d()
    fun getAppliedVolts(): Double = 0.0
    fun getCurrentAmps(): Double = 0.0
    fun getVelocityRadPerSec(): Double = 0.0

    fun updateInputs(inputs: NEOIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    fun setBrakeMode(enabled: Boolean) {}

    fun resetEncoder(newAngle: Rotation2d = Rotation2d()) {}

    fun stop() {}
}