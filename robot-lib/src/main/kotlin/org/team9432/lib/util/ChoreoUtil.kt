package org.team9432.lib.util

import com.choreo.lib.ChoreoTrajectory
import com.choreo.lib.ChoreoTrajectoryState
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import kotlinx.coroutines.suspendCancellableCoroutine
import org.team9432.lib.LibraryState
import org.team9432.lib.RobotPeriodicManager
import kotlin.coroutines.resume

object ChoreoUtil {
    fun ChoreoTrajectory.getAutoFlippedInitialPose(): Pose2d = allianceSwitch(blue = initialPose, red = flippedInitialPose)

    /**
     * @param trajectory The trajectory to follow.
     * @param controlFunction A function to calculate the desired [ChassisSpeeds] that should be applied to the robot based on the given [ChoreoTrajectoryState].
     * @param outputChassisSpeeds A function that consumes the target robot-relative chassis speeds
     * and commands them to the robot.
     */
    suspend fun choreoSwerveAction(
        trajectory: ChoreoTrajectory,
        controlFunction: (ChoreoTrajectoryState) -> ChassisSpeeds,
        outputChassisSpeeds: (ChassisSpeeds) -> Unit,
    ) {
        val shouldMirrorTrajectory = LibraryState.alliance == DriverStation.Alliance.Red // Mirror if on the red alliance

        suspendCancellableCoroutine { cont ->
            val timer = Timer()
            timer.restart()

            val periodic = RobotPeriodicManager.startPeriodic {
                val targetChassisSpeeds = controlFunction.invoke(trajectory.sample(timer.get(), shouldMirrorTrajectory))

                outputChassisSpeeds.invoke(targetChassisSpeeds)

                if (timer.hasElapsed(trajectory.totalTime)) {
                    timer.stop()
                    outputChassisSpeeds.invoke(trajectory.finalState.chassisSpeeds)

                    this.stopPeriodic()
                    cont.resume(Unit)
                }
            }

            cont.invokeOnCancellation {
                timer.stop()
                outputChassisSpeeds.invoke(ChassisSpeeds())

                periodic.stopPeriodic()
            }
        }
    }

    fun choreoSwerveController(
        xController: PIDController,
        yController: PIDController,
        rotationController: PIDController,
        poseSupplier: () -> Pose2d,
    ): (ChoreoTrajectoryState) -> ChassisSpeeds {
        rotationController.enableContinuousInput(-Math.PI, Math.PI)

        return { referenceState: ChoreoTrajectoryState ->
            val pose = poseSupplier.invoke()

            val xFF = referenceState.velocityX
            val yFF = referenceState.velocityY
            val rotationFF = referenceState.angularVelocity

            val xFeedback = xController.calculate(pose.x, referenceState.x)
            val yFeedback = yController.calculate(pose.y, referenceState.y)
            val rotationFeedback = rotationController.calculate(pose.rotation.radians, referenceState.heading)

            ChassisSpeeds.fromFieldRelativeSpeeds(
                xFF + xFeedback,
                yFF + yFeedback,
                rotationFF + rotationFeedback,
                pose.rotation
            )
        }
    }
}