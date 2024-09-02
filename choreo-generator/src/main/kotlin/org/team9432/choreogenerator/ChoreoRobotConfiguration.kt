package org.team9432.choreogenerator

import org.team9432.choreogenerator.json.JsonRobotConfiguration
import org.team9432.lib.unit.Length
import org.team9432.lib.unit.inMeters

data class ChoreoRobotConfiguration(
    val mass: Double,
    val rotationalInertia: Int,
    val motorMaxTorque: Double,
    val motorMaxVelocity: Int,
    val gearing: Double,
    val wheelbase: Length,
    val trackWidth: Length,
    val bumperLength: Length,
    val bumperWidth: Length,
    val wheelRadius: Length,
) {
    internal fun getJsonConfiguration() = JsonRobotConfiguration(
        mass,
        rotationalInertia,
        motorMaxTorque,
        motorMaxVelocity,
        gearing,
        wheelbase.inMeters,
        trackWidth.inMeters,
        bumperLength.inMeters,
        bumperWidth.inMeters,
        wheelRadius.inMeters
    )
}