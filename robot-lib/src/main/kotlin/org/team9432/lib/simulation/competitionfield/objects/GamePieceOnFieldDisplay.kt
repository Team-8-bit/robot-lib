package org.team9432.lib.simulation.competitionfield.objects

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import org.team9432.lib.simulation.competitionfield.CompetitionFieldVisualizer.Object2dOnFieldDisplay

/**
 * displays a game piece on field to Advantage Scope
 * since game pieces MUST be displayed as 3d objects in Advantage Scope
 * we have to convert the 2d pose of the game piece to a 3d pose
 */
interface GamePieceOnFieldDisplay: Object2dOnFieldDisplay {
    override val pose3d: Pose3d
        get() {
            val pose2d = objectOnFieldPose2d
            val translation3d = Translation3d(
                pose2d.x,
                pose2d.y,
                gamePieceHeight / 2
            )
            return Pose3d(translation3d, Rotation3d())
        }

    /**
     * @return the height of the game piece when standing from ground, in meters
     */
    val gamePieceHeight: Double
}
