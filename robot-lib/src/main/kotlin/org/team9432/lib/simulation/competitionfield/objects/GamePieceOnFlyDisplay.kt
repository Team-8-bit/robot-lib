package org.team9432.lib.simulation.competitionfield.objects

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.wpilibj.Timer
import org.team9432.lib.simulation.competitionfield.CompetitionFieldVisualizer.ObjectOnFieldDisplay
import kotlin.math.atan2

abstract class GamePieceOnFlyDisplay(private val shooterPosition: Translation3d, private val targetedPosition: Translation3d, private val flightTimeSeconds: Double): ObjectOnFieldDisplay {
    private val startTimeSeconds = Timer.getFPGATimestamp()
    private val gamePieceRotation: Rotation3d

    init {
        val displacementToTarget = targetedPosition.minus(shooterPosition)
        val yaw = displacementToTarget.toTranslation2d().angle.radians
        val pitch = -atan2(displacementToTarget.z, displacementToTarget.toTranslation2d().norm)
        this.gamePieceRotation = Rotation3d(0.0, pitch, yaw)
    }

    override val pose3d: Pose3d
        get() = Pose3d(shooterPosition.interpolate(targetedPosition, t), gamePieceRotation)

    val isReached: Boolean
        get() = t >= 1

    val t: Double
        get() = (Timer.getFPGATimestamp() - startTimeSeconds) / flightTimeSeconds
}
