package org.team9432.lib.simulation.competitionfield.simulations

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.util.Units
import org.dyn4j.geometry.Vector2
import org.team9432.lib.simulation.math.GeometryConvertor
import org.team9432.lib.simulation.RobotSimulationProfile
import org.team9432.lib.simulation.SimulatedGyro
import org.team9432.lib.simulation.SimulatedSwerveModule
import org.team9432.lib.unit.inMeters
import java.util.function.Consumer
import kotlin.math.*

/**
 * simulates the behavior of our robot
 * it has all the physics behavior as a simulated holonomic chassis
 * in addition to that, it simulates the swerve module behaviors
 * the class is like the bridge between ModuleIOSim and HolonomicChassisSimulation
 * it reads the motor power from ModuleIOSim
 * and feed the result of the physics simulation back to ModuleIOSim, to simulate the odometry encoders' readings
 */
class SwerveDriveSimulation(
    profile: RobotSimulationProfile,
    private val gyroIOSim: SimulatedGyro,
    frontLeft: SimulatedSwerveModule,
    frontRight: SimulatedSwerveModule,
    backLeft: SimulatedSwerveModule,
    backRight: SimulatedSwerveModule,
    startingPose: Pose2d,
    private val resetOdometryCallBack: Consumer<Pose2d>,
): HolonomicChassisSimulation(profile, startingPose) {
    private val modules: Array<SimulatedSwerveModule> = arrayOf(frontLeft, frontRight, backLeft, backRight)

    private val maxFrictionForcePerModule: Double = profile.frictionForce / profile.moduleTranslations.size

    init {
        resetOdometryCallBack.accept(objectOnFieldPose2d)
    }

    override fun updateSimulationSubTick(tickNum: Int, tickSeconds: Double) {
        for (i in modules.indices) moduleSimulationSubTick(
            objectOnFieldPose2d,
            modules[i],
            profile.moduleTranslations[i],
            tickNum, tickSeconds
        )

        simulateFrictionForce()

        gyroSimulationSubTick(
            super.objectOnFieldPose2d.rotation,
            super.getAngularVelocity(),
            tickNum
        )
    }

    private fun moduleSimulationSubTick(
        robotWorldPose: Pose2d,
        module: SimulatedSwerveModule,
        moduleTranslationOnRobot: Translation2d,
        tickNum: Int,
        tickPeriodSeconds: Double,
    ) {
        /* update the DC motor simulation of the steer */
        module.updateSteerSim(tickPeriodSeconds)

        /* simulate the propelling force of the module */
        val moduleWorldFacing: Rotation2d = module.getSimulationSwerveState().angle.plus(robotWorldPose.rotation)
        val moduleWorldPosition: Vector2 = GeometryConvertor.toDyn4jVector2(
            robotWorldPose.translation
                .plus(moduleTranslationOnRobot.rotateBy(robotWorldPose.rotation))
        )
        var actualPropellingForceOnFloorNewtons: Double = module.getSimulationTorque() / profile.wheelRadius.inMeters
        val skidding: Boolean = abs(actualPropellingForceOnFloorNewtons) > maxFrictionForcePerModule
        if (skidding) actualPropellingForceOnFloorNewtons = maxFrictionForcePerModule.withSign(actualPropellingForceOnFloorNewtons)
        super.applyForce(
            Vector2.create(actualPropellingForceOnFloorNewtons, moduleWorldFacing.radians),
            moduleWorldPosition
        )


        val floorVelocity: Vector2 = super.getLinearVelocity(moduleWorldPosition)
        val floorVelocityProjectionOnWheelDirectionMPS: Double = floorVelocity.magnitude * cos(floorVelocity.getAngleBetween(Vector2(moduleWorldFacing.radians)))

        if (skidding)  /* if the chassis is skidding, the toque will cause the wheels to spin freely */
            module.physicsSimulationResults.driveWheelFinalVelocityRadPerSec += module.getSimulationTorque() / profile.driveInertia * tickPeriodSeconds
        else  /* otherwise, the floor velocity is projected to the wheel */
            module.physicsSimulationResults.driveWheelFinalVelocityRadPerSec = floorVelocityProjectionOnWheelDirectionMPS / profile.wheelRadius.inMeters

        module.physicsSimulationResults.odometrySteerPositions[tickNum] = module.getSimulationSwerveState().angle
        module.physicsSimulationResults.driveWheelFinalRevolutions += Units.radiansToRotations(
            module.physicsSimulationResults.driveWheelFinalVelocityRadPerSec * tickPeriodSeconds
        )
        module.physicsSimulationResults.odometryDriveWheelRevolutions[tickNum] = module.physicsSimulationResults.driveWheelFinalRevolutions
    }

    private fun simulateFrictionForce() {
        val speedsDifference: ChassisSpeeds = differenceBetweenFloorAndFreeSpeed
        val translationalSpeedsDifference = Translation2d(speedsDifference.vxMetersPerSecond, speedsDifference.vyMetersPerSecond)
        val forceMultiplier: Double = min(translationalSpeedsDifference.norm * 3, 1.0)
        super.applyForce(
            Vector2.create(
                forceMultiplier * profile.frictionForce,
                translationalSpeedsDifference.angle.radians
            )
        )

        if (abs(desiredSpeedsFieldRelative.omegaRadiansPerSecond) / profile.maxAngularVelocity < 0.01) simulateChassisRotationalBehavior(0.0)
    }

    private val differenceBetweenFloorAndFreeSpeed: ChassisSpeeds
        get() {
            var chassisFreeSpeedsFieldRelative: ChassisSpeeds = freeSpeedsFieldRelative

            val freeSpeedMagnitude: Double = hypot(chassisFreeSpeedsFieldRelative.vxMetersPerSecond, chassisFreeSpeedsFieldRelative.vyMetersPerSecond)
            val floorSpeedMagnitude: Double = hypot(measuredChassisSpeedsFieldRelative.vxMetersPerSecond, measuredChassisSpeedsFieldRelative.vyMetersPerSecond)
            if (freeSpeedMagnitude > floorSpeedMagnitude) chassisFreeSpeedsFieldRelative = chassisFreeSpeedsFieldRelative.times(floorSpeedMagnitude / freeSpeedMagnitude)

            return chassisFreeSpeedsFieldRelative.minus(measuredChassisSpeedsFieldRelative)
        }

    private val freeSpeedsFieldRelative: ChassisSpeeds
        get() = ChassisSpeeds.fromRobotRelativeSpeeds(
            profile.kinematics.toChassisSpeeds(*modules.map(SimulatedSwerveModule::getSimulationSwerveState).toTypedArray()),
            objectOnFieldPose2d.rotation
        )

    private val desiredSpeedsFieldRelative: ChassisSpeeds
        get() {
            return ChassisSpeeds.fromRobotRelativeSpeeds(
                profile.kinematics.toChassisSpeeds(*modules.map(SimulatedSwerveModule::getDesiredSimulationSwerveState).toTypedArray()),
                objectOnFieldPose2d.rotation
            )
        }

    private fun gyroSimulationSubTick(
        currentFacing: Rotation2d,
        angularVelocityRadPerSec: Double,
        tickNum: Int,
    ) {
        val results: SimulatedGyro.GyroPhysicsSimulationResults = gyroIOSim.gyroPhysicsSimulationResults
        results.robotAngularVelocityRadPerSec = angularVelocityRadPerSec
        results.odometryYawPositions[tickNum] = currentFacing
        results.hasReading = true
    }
}