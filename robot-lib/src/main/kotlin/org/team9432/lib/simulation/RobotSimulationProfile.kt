package org.team9432.lib.simulation

import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import org.team9432.lib.unit.Length
import kotlin.math.hypot

data class RobotSimulationProfile(
    val moduleTranslations: List<Translation2d>,
    val wheelRadius: Length,
    val kinematics: SwerveDriveKinematics,
    val robotMaxVelocity: Double,
    val robotMaxAcceleration: Double,
    val maxAngularVelocity: Double,
    val robotMass: Double,
    val width: Double,
    val height: Double,
    val robotBumperToCenterOffset: Transform2d,
    val frictionForce: Double,
    val angularFrictionAcceleration: Double,
    val dampingCoefficient: Double = 0.3,
    val driveInertia: Double = 0.01
) {
    val propellingForce: Double = robotMaxAcceleration * robotMass
    val linearVelocityDamping: Double = robotMaxAcceleration / robotMaxVelocity * dampingCoefficient
    val maxAngularAcceleration: Double = robotMaxAcceleration / (hypot(width, height) / 2)
    val angularDamping: Double = maxAngularAcceleration / maxAngularVelocity * dampingCoefficient
}