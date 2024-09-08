package org.team9432.lib.simulation.competitionfield.simulations

import edu.wpi.first.math.geometry.Translation2d
import org.dyn4j.collision.CollisionBody
import org.dyn4j.collision.Fixture
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.dynamics.contact.Contact
import org.dyn4j.dynamics.contact.SolvedContact
import org.dyn4j.geometry.*
import org.dyn4j.world.ContactCollisionData
import org.dyn4j.world.listener.ContactListener
import org.team9432.lib.simulation.competitionfield.objects.GamePieceInSimulation
import org.team9432.lib.simulation.math.GeometryConvertor
import java.util.*
import java.util.function.BooleanSupplier

open class IntakeSimulation(shape: Convex, capacity: Int, private val intakeRunningSupplier: BooleanSupplier): BodyFixture(shape) {
    private val capacity: Int
    var gamePieceCount = 0

    val gamePiecesToRemove: Queue<GamePieceInSimulation>

    /**
     * Creates an intake simulation
     * the intake is considered a line segment on the robot
     * any game piece that touches the line will be grabbed
     *
     * @param startPointOnRobot     the start point of the segment, in relative to the robot
     * @param endPointOnRobot       the end point of the segment, in relative to the robot
     * @param capacity              the amount of game-pieces that can be hold in the intake\
     * @param intakeRunningSupplier whether is intake is running now
     */
    constructor(startPointOnRobot: Translation2d, endPointOnRobot: Translation2d, capacity: Int, intakeRunningSupplier: BooleanSupplier): this(
        Segment(GeometryConvertor.toDyn4jVector2(startPointOnRobot), GeometryConvertor.toDyn4jVector2(endPointOnRobot)),
        capacity,
        intakeRunningSupplier
    )

    /**
     * Creates an intake simulation
     * the intake is fixed shape on the robot
     * any game piece that touches the line will be grabbed
     *
     * @param shape the shape of the intake
     * @param capacity              the amount of game-pieces that can be hold in the intake\
     * @param intakeRunningSupplier whether is intake is running now
     */
    init {
        require(capacity <= 100) { "capacity too large, max is 100" }
        this.capacity = capacity

        this.gamePiecesToRemove = ArrayDeque(capacity)
    }

    inner class GamePieceContactListener: ContactListener<Body> {
        override fun begin(collision: ContactCollisionData<Body>, contact: Contact) {
            if (!intakeRunningSupplier.asBoolean) return
            if (gamePieceCount == capacity) return

            val collisionBody1: CollisionBody<*> = collision.body1
            val collisionBody2: CollisionBody<*> = collision.body2
            val fixture1: Fixture = collision.fixture1
            val fixture2: Fixture = collision.fixture2

            if (collisionBody1 is GamePieceInSimulation && fixture2 === this@IntakeSimulation) flagGamePieceForRemoval(collisionBody1)
            else if (collisionBody2 is GamePieceInSimulation && fixture1 === this@IntakeSimulation) flagGamePieceForRemoval(collisionBody2)
        }

        private fun flagGamePieceForRemoval(gamePiece: GamePieceInSimulation) {
            gamePiecesToRemove.add(gamePiece)
            gamePieceCount++
        }

        /* functions not used */
        override fun persist(collision: ContactCollisionData<Body>?, oldContact: Contact, newContact: Contact) {}
        override fun end(collision: ContactCollisionData<Body>?, contact: Contact) {}
        override fun destroyed(collision: ContactCollisionData<Body>?, contact: Contact) {}
        override fun collision(collision: ContactCollisionData<Body>?) {}
        override fun preSolve(collision: ContactCollisionData<Body>?, contact: Contact) {}
        override fun postSolve(collision: ContactCollisionData<Body>?, contact: SolvedContact) {}
    }

    val gamePieceContactListener: GamePieceContactListener
        get() = GamePieceContactListener()

    fun clearGamePiecesToRemoveQueue() {
        this.gamePieceCount += gamePiecesToRemove.size
        gamePiecesToRemove.clear()
    }
}
