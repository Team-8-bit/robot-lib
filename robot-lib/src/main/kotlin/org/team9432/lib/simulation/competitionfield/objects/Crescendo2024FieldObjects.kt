package org.team9432.lib.simulation.competitionfield.objects

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.util.Units
import org.dyn4j.geometry.Geometry
import org.team9432.lib.simulation.FieldConstants
import org.team9432.lib.util.applyFlip

/**
 * a set of game pieces of the 2024 game "Crescendo"
 */
object Crescendo2024FieldObjects {
    /* https://www.andymark.com/products/frc-2024-am-4999 */
    private val NOTE_HEIGHT = Units.inchesToMeters(2.0)
    private val NOTE_DIAMETER = Units.inchesToMeters(14.0)

    /**
     * a static note on field
     * it is displayed on the dashboard and telemetry,
     * but it does not appear in the simulation.
     * meaning that, it does not have collision space and isn't involved in intake simulation
     */
    class NoteOnFieldStatic(private val initialPosition: Translation2d): GamePieceOnFieldDisplay {
        override val objectOnFieldPose2d: Pose2d
            get() = Pose2d(initialPosition, Rotation2d())

        override val typeName: String
            get() = "Note"

        override val gamePieceHeight: Double
            get() = NOTE_HEIGHT
    }

    /**
     * a simulated note on field
     * has collision space, and can be "grabbed" by an intake simulation
     */
    class NoteOnFieldSimulated(initialPosition: Translation2d): GamePieceInSimulation(initialPosition, Geometry.createCircle(NOTE_DIAMETER / 2)) {
        override val gamePieceHeight: Double
            get() = NOTE_HEIGHT

        override val typeName: String
            get() = "Note"
    }

    /**
     * a note that is flying from a shooter to the speaker
     * the flight is simulated by a simple linear animation
     */
    class NoteOnFly(shooterPosition: Translation3d, flightTimeSeconds: Double): GamePieceOnFlyDisplay(
        shooterPosition,
        FieldConstants.SPEAKER_POSE_BLUE.applyFlip(),
        flightTimeSeconds
    ) {
        override val typeName: String
            get() = "Note"
    }
}
