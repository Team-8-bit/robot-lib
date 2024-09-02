package org.team9432.lib.util

import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.util.Units
import kotlin.math.abs
import kotlin.math.hypot

fun ChassisSpeeds.velocityLessThan(metersPerSecond: Double, rotationsPerSecond: Double) =
    hypot(vxMetersPerSecond, vyMetersPerSecond) < metersPerSecond && abs(Units.radiansToRotations(omegaRadiansPerSecond)) < rotationsPerSecond