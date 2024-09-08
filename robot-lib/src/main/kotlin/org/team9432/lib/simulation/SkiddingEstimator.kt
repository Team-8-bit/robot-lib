package org.team9432.lib.simulation

import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModuleState
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

object SkiddingEstimator {
    /**
     * the method comes from 1690's [online software session](https://youtu.be/N6ogT5DjGOk?feature=shared&t=1674) gets the
     * skidding ratio from the latest , that can be used to determine how much the chassis is skidding
     * the skidding ratio is defined as the ratio between the maximum and minimum magnitude of the
     * "translational" part of the speed of the modules
     *
     * @param swerveStatesMeasured  the swerve states measured from the modules
     * @param swerveDriveKinematics the kinematics
     * @return the skidding ratio, maximum/minimum, ranges from [1,INFINITY)
     */
    fun getSkiddingRatio(
        swerveStatesMeasured: Array<SwerveModuleState>, swerveDriveKinematics: SwerveDriveKinematics,
    ): Double {
        val angularVelocityOmegaMeasured: Double =
            swerveDriveKinematics.toChassisSpeeds(*swerveStatesMeasured).omegaRadiansPerSecond
        val swerveStatesRotationalPart: Array<SwerveModuleState> =
            swerveDriveKinematics.toSwerveModuleStates(
                ChassisSpeeds(0.0, 0.0, angularVelocityOmegaMeasured)
            )

        val swerveStatesTranslationalPartMagnitudes =
            DoubleArray(swerveStatesMeasured.size) { index ->
                val swerveStateMeasuredAsVector: Translation2d = convertSwerveStateToVelocityVector(swerveStatesMeasured.get(index))
                val swerveStatesRotationalPartAsVector: Translation2d = convertSwerveStateToVelocityVector(swerveStatesRotationalPart[index])
                val swerveStatesTranslationalPartAsVector: Translation2d = swerveStateMeasuredAsVector.minus(swerveStatesRotationalPartAsVector)
                swerveStatesTranslationalPartAsVector.norm
            }

        var maximumTranslationalSpeed: Double = 0.0
        var minimumTranslationalSpeed: Double = Double.POSITIVE_INFINITY
        for (translationalSpeed: Double in swerveStatesTranslationalPartMagnitudes) {
            maximumTranslationalSpeed = max(maximumTranslationalSpeed, translationalSpeed)
            minimumTranslationalSpeed = min(minimumTranslationalSpeed, translationalSpeed)
        }

        return maximumTranslationalSpeed / minimumTranslationalSpeed
    }

    private fun convertSwerveStateToVelocityVector(
        swerveModuleState: SwerveModuleState,
    ): Translation2d {
        return Translation2d(swerveModuleState.speedMetersPerSecond, swerveModuleState.angle)
    }

    /**
     * Estimates the skidding of the chassis from the latest measured swerve states
     *
     * @return the standard deviation of the current swerve state, from the ideal swerve state
     */
    fun getSkiddingStandardDeviation(
        measuredSwerveStates: Array<SwerveModuleState>, swerveDriveKinematics: SwerveDriveKinematics,
    ): Double {
        val measuredChassisSpeed: ChassisSpeeds =
            swerveDriveKinematics.toChassisSpeeds(*measuredSwerveStates)
        val idealSwerveStatesGivenNoSkidding: Array<SwerveModuleState> =
            swerveDriveKinematics.toSwerveModuleStates(measuredChassisSpeed)

        var totalSquaredDeviation: Double = 0.0
        for (i in 0..3) totalSquaredDeviation +=
            getSquaredDifferenceBetweenTwoSwerveStates(
                measuredSwerveStates.get(i), idealSwerveStatesGivenNoSkidding.get(i)
            )

        val variance: Double = totalSquaredDeviation / 4
        return sqrt(variance)
    }

    /**
     * gets the squared difference between the velocity vector of two swerve states
     *
     * @return the squared difference, in (meters/seconds)^2,
     */
    private fun getSquaredDifferenceBetweenTwoSwerveStates(
        swerveModuleState1: SwerveModuleState, swerveModuleState2: SwerveModuleState,
    ): Double {
        val swerveState1VelocityVector: Translation2d =
            Translation2d(swerveModuleState1.speedMetersPerSecond, swerveModuleState1.angle)
        val swerveState2VelocityVector: Translation2d =
            Translation2d(swerveModuleState2.speedMetersPerSecond, swerveModuleState2.angle)

        return swerveState1VelocityVector.getDistance(swerveState2VelocityVector).pow(2.0)
    }
}
