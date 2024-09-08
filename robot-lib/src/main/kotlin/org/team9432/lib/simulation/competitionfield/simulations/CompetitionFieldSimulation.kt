package org.team9432.lib.simulation.competitionfield.simulations

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Translation2d
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.MassType
import org.dyn4j.world.PhysicsWorld
import org.dyn4j.world.World
import org.team9432.lib.Library
import org.team9432.lib.simulation.SIMULATION_TICKS_IN_1_PERIOD
import org.team9432.lib.simulation.competitionfield.CompetitionFieldVisualizer
import org.team9432.lib.simulation.competitionfield.objects.GamePieceInSimulation
import org.team9432.lib.simulation.math.GeometryConvertor

/**
 * this class simulates the physical behavior of all the objects on field
 * should only be created during a robot simulation (not in real or replay mode)
 */
abstract class CompetitionFieldSimulation(mainRobot: HolonomicChassisSimulation, obstaclesMap: FieldObstaclesMap) {
    private val physicsWorld: World<Body>
    val competitionField: CompetitionFieldVisualizer
    private val robotSimulations: MutableSet<HolonomicChassisSimulation> = HashSet()
    val mainRobot: HolonomicChassisSimulation
    protected val gamePieces: MutableSet<GamePieceInSimulation>

    private val intakeSimulations: MutableList<IntakeSimulation> = ArrayList()

    init {
        this.competitionField = CompetitionFieldVisualizer(mainRobot)
        this.mainRobot = mainRobot
        this.physicsWorld = World()
        physicsWorld.setGravity(PhysicsWorld.ZERO_GRAVITY)
        for (obstacle: Body in obstaclesMap.obstacles) physicsWorld.addBody(obstacle)
        this.gamePieces = HashSet()

        physicsWorld.addBody(mainRobot)
        robotSimulations.add(mainRobot)
    }

    fun updateSimulationWorld() {
        competitionPeriodic()
        val subPeriodSeconds: Double = Library.robotPeriod / SIMULATION_TICKS_IN_1_PERIOD
        // move through 5 sub-periods in each update
        for (i in 0 until SIMULATION_TICKS_IN_1_PERIOD) {
            physicsWorld.step(1, subPeriodSeconds)
            for (robotSimulation: HolonomicChassisSimulation in robotSimulations) robotSimulation.updateSimulationSubTick(i, subPeriodSeconds)
        }

        for (intakeSimulation: IntakeSimulation in intakeSimulations) while (!intakeSimulation.gamePiecesToRemove.isEmpty()) this.removeGamePiece(intakeSimulation.gamePiecesToRemove.poll())
    }

    fun addRobot(chassisSimulation: HolonomicChassisSimulation) {
        physicsWorld.addBody(chassisSimulation)
        robotSimulations.add(chassisSimulation)
        competitionField.addObject(chassisSimulation)
    }

    fun registerIntake(intakeSimulation: IntakeSimulation) {
        intakeSimulations.add(intakeSimulation)
        mainRobot.addFixture(intakeSimulation)
        physicsWorld.addContactListener(intakeSimulation.gamePieceContactListener)
    }

    fun addGamePiece(gamePieceInSimulation: GamePieceInSimulation) {
        physicsWorld.addBody(gamePieceInSimulation)
        competitionField.addObject(gamePieceInSimulation)
        gamePieces.add(gamePieceInSimulation)
    }

    fun removeGamePiece(gamePieceInSimulation: GamePieceInSimulation) {
        physicsWorld.removeBody(gamePieceInSimulation)
        competitionField.deleteObject(gamePieceInSimulation)
        gamePieces.remove(gamePieceInSimulation)
    }

    fun clearGamePieces() {
        for (gamePiece: GamePieceInSimulation in this.gamePieces) {
            physicsWorld.removeBody(gamePiece)
            competitionField.clearObjectsWithGivenType(gamePiece.typeName)
        }
        gamePieces.clear()
    }

    fun resetFieldForAuto() {
        clearGamePieces()
        placeGamePiecesOnField()
    }

    /**
     * place all game pieces on the field (for autonomous)
     */
    abstract fun placeGamePiecesOnField()

    /**
     * update the score counts & human players periodically
     * implement this method in current year's simulation
     */
    abstract fun competitionPeriodic()

    /**
     * stores the obstacles on a competition field, which includes the border and the game pieces
     */
    abstract class FieldObstaclesMap {
        val obstacles: MutableList<Body> = ArrayList()

        protected fun addBorderLine(startingPoint: Translation2d, endingPoint: Translation2d) {
            val obstacle: Body = getObstacle(
                Geometry.createSegment(
                    GeometryConvertor.toDyn4jVector2(startingPoint),
                    GeometryConvertor.toDyn4jVector2(endingPoint)
                )
            )
            obstacles.add(obstacle)
        }

        protected fun addRectangularObstacle(width: Double, height: Double, pose: Pose2d) {
            val obstacle: Body = getObstacle(
                Geometry.createRectangle(
                    width, height
                )
            )

            obstacle.getTransform().set(GeometryConvertor.toDyn4jTransform(pose))
            obstacles.add(obstacle)
        }

        companion object {
            private fun getObstacle(shape: Convex): Body {
                val obstacle: Body = Body()
                obstacle.setMass(MassType.INFINITE)
                val fixture: BodyFixture = obstacle.addFixture(shape)
                fixture.setFriction(0.8)
                fixture.setRestitution(0.6)
                return obstacle
            }
        }
    }
}
