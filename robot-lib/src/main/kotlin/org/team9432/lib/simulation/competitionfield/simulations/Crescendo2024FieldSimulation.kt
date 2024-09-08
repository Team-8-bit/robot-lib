package org.team9432.lib.simulation.competitionfield.simulations

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import org.team9432.lib.simulation.FieldConstants.FIELD_WIDTH
import org.team9432.lib.simulation.competitionfield.objects.Crescendo2024FieldObjects.NoteOnFieldSimulated
import org.team9432.lib.util.applyFlip

/**
 * field simulation for 2024 competition
 */
class Crescendo2024FieldSimulation(robot: HolonomicChassisSimulation): CompetitionFieldSimulation(robot, CrescendoFieldObstaclesMap()) {
    override fun placeGamePiecesOnField() {
        for (notePosition: Translation2d in NOTE_INITIAL_POSITIONS) super.addGamePiece(NoteOnFieldSimulated(notePosition))
    }

    /**
     * the obstacles on the 2024 competition field
     */
    class CrescendoFieldObstaclesMap: FieldObstaclesMap() {
        init {
            //left wall
            super.addBorderLine(
                Translation2d(0.0, 1.0),
                Translation2d(0.0, 4.51)
            )
            super.addBorderLine(
                Translation2d(0.0, 4.51),
                Translation2d(0.9, 5.0)
            )

            super.addBorderLine(
                Translation2d(0.9, 5.0),
                Translation2d(0.9, 6.05)
            )

            super.addBorderLine(
                Translation2d(0.9, 6.05),
                Translation2d(0.0, 6.5)
            )
            super.addBorderLine(
                Translation2d(0.0, 6.5),
                Translation2d(0.0, 8.2)
            )


            // upper wall
            super.addBorderLine(
                Translation2d(0.0, 8.12),
                Translation2d(FIELD_WIDTH, 8.12)
            )


            // righter wall 
            super.addBorderLine(
                Translation2d(FIELD_WIDTH, 1.0),
                Translation2d(FIELD_WIDTH, 4.51)
            )
            super.addBorderLine(
                Translation2d(FIELD_WIDTH, 4.51),
                Translation2d(FIELD_WIDTH - 0.9, 5.0)
            )
            super.addBorderLine(
                Translation2d(FIELD_WIDTH - 0.9, 5.0),
                Translation2d(FIELD_WIDTH - 0.9, 6.05)
            )
            super.addBorderLine(
                Translation2d(FIELD_WIDTH - 0.9, 6.05),
                Translation2d(FIELD_WIDTH, 6.5)
            )
            super.addBorderLine(
                Translation2d(FIELD_WIDTH, 6.5),
                Translation2d(FIELD_WIDTH, 8.2)
            )

            // lower wall
            super.addBorderLine(
                Translation2d(1.92, 0.0),
                Translation2d(FIELD_WIDTH - 1.92, 0.0)
            )

            // red source wall
            super.addBorderLine(
                Translation2d(1.92, 0.0),
                Translation2d(0.0, 1.0)
            )

            // blue source wall
            super.addBorderLine(
                Translation2d(FIELD_WIDTH - 1.92, 0.0),
                Translation2d(FIELD_WIDTH, 1.0)
            )

            // blue state
            super.addRectangularObstacle(
                0.35, 0.35,
                Pose2d(3.4, 4.1, Rotation2d())
            )
            super.addRectangularObstacle(
                0.35, 0.35,
                Pose2d(5.62, 4.1 - 1.28, Rotation2d.fromDegrees(30.0))
            )
            super.addRectangularObstacle(
                0.35, 0.35,
                Pose2d(5.62, 4.1 + 1.28, Rotation2d.fromDegrees(60.0))
            )

            // red stage
            super.addRectangularObstacle(
                0.35, 0.35,
                Pose2d(FIELD_WIDTH - 3.4, 4.1, Rotation2d())
            )
            super.addRectangularObstacle(
                0.35, 0.35,
                Pose2d(FIELD_WIDTH - 5.62, 4.1 - 1.28, Rotation2d.fromDegrees(60.0))
            )
            super.addRectangularObstacle(
                0.35, 0.35,
                Pose2d(FIELD_WIDTH - 5.62, 4.1 + 1.28, Rotation2d.fromDegrees(30.0))
            )
        }
    }

    override fun competitionPeriodic() {
        simulateHumanPlayer()
    }

    private var previousThrowTimeSeconds = 0.0
    private var previousPickupTimeSeconds = 0.0
    private fun simulateHumanPlayer() {
        if (!DriverStation.isTeleopEnabled()) return

        if (Timer.getFPGATimestamp() - previousThrowTimeSeconds < 1) return

        val sourcePosition: Translation2d = BLUE_SOURCE_POSITION.applyFlip()
        /* if there is any game-piece 0.5 meters within the human player station, we don't throw a new note */
        if (super.gamePieces.any { it.objectOnFieldPose2d.translation.getDistance(sourcePosition) < 1 }) {
            previousPickupTimeSeconds = Timer.getFPGATimestamp()
            return
        }

        // Return if it just became possible to drop a game piece within the last four seconds
        if (Timer.getFPGATimestamp() - previousPickupTimeSeconds < 4) return

        /* otherwise, place a note */
        addGamePiece(NoteOnFieldSimulated(sourcePosition))
        previousThrowTimeSeconds = Timer.getFPGATimestamp()
    }

    companion object {
        private val BLUE_SOURCE_POSITION: Translation2d = Translation2d(15.6, 0.8)


        private val NOTE_INITIAL_POSITIONS: Array<Translation2d> = arrayOf(
            Translation2d(2.9, 4.1),
            Translation2d(2.9, 5.55),
            Translation2d(2.9, 7.0),

            Translation2d(8.27, 0.75),
            Translation2d(8.27, 2.43),
            Translation2d(8.27, 4.1),
            Translation2d(8.27, 5.78),
            Translation2d(8.27, 7.46),

            Translation2d(13.64, 4.1),
            Translation2d(13.64, 5.55),
            Translation2d(13.64, 7.0),
        )
    }
}
