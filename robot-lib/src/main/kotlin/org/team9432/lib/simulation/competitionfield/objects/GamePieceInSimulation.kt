package org.team9432.lib.simulation.competitionfield.objects

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Translation2d
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.MassType
import org.team9432.lib.simulation.math.GeometryConvertor

/**
 * simulates the behavior of gamepiece on field.
 * game pieces HAVE collision spaces.
 * they can also be "grabbed" by an Intake Simulation
 * the game piece will also be displayed on advantage scope (once registered in CompetitionFieldSimulation)
 */
abstract class GamePieceInSimulation @JvmOverloads constructor(initialPosition: Translation2d, shape: Convex, mass: Double = DEFAULT_MASS): Body(),
    GamePieceOnFieldDisplay {
    init {
        val bodyFixture = super.addFixture(shape)
        bodyFixture.friction = EDGE_COEFFICIENT_OF_FRICTION
        bodyFixture.friction = EDGE_COEFFICIENT_OF_RESTITUTION
        bodyFixture.density = mass / shape.area
        super.setMass(MassType.NORMAL)

        super.translate(GeometryConvertor.toDyn4jVector2(initialPosition))

        super.setLinearDamping(LINEAR_DAMPING)
        super.setAngularDamping(ANGULAR_DAMPING)
        super.setBullet(true)
    }

    override val objectOnFieldPose2d: Pose2d
        get() = GeometryConvertor.toWpilibPose2d(super.getTransform())

    companion object {
        /**
         * for convenience, we assume all game pieces have the following properties
         */
        const val DEFAULT_MASS: Double = 0.2
        const val LINEAR_DAMPING: Double = 2.5
        const val ANGULAR_DAMPING: Double = 5.0
        const val EDGE_COEFFICIENT_OF_FRICTION: Double = 0.8
        const val EDGE_COEFFICIENT_OF_RESTITUTION: Double = 0.3
    }
}
