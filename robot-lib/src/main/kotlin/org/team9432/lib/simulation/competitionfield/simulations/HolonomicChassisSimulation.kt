package org.team9432.lib.simulation.competitionfield.simulations

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.Force
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Vector2
import org.team9432.lib.simulation.BUMPER_COEFFICIENT_OF_FRICTION
import org.team9432.lib.simulation.BUMPER_COEFFICIENT_OF_RESTITUTION
import org.team9432.lib.simulation.competitionfield.objects.RobotOnFieldDisplay
import org.team9432.lib.simulation.math.GeometryConvertor
import org.team9432.lib.simulation.math.MapleCommonMath
import org.team9432.lib.simulation.RobotSimulationProfile
import kotlin.math.abs
import kotlin.math.withSign

/**
 * simulates the physics behavior of holonomic chassis,
 * with respect to its collision space, friction and motor propelling forces
 */
abstract class HolonomicChassisSimulation(val profile: RobotSimulationProfile, startingPose: Pose2d): Body(), RobotOnFieldDisplay {
    init {
        /* width and height in world reference is flipped */
        val WIDTH_IN_WORLD_REFERENCE: Double = profile.height
        val HEIGHT_IN_WORLD_REFERENCE: Double = profile.width
        super.addFixture(
            Geometry.createRectangle(WIDTH_IN_WORLD_REFERENCE, HEIGHT_IN_WORLD_REFERENCE),
            profile.robotMass / (profile.height * profile.width),
            BUMPER_COEFFICIENT_OF_FRICTION,
            BUMPER_COEFFICIENT_OF_RESTITUTION
        )

        super.setMass(MassType.NORMAL)
        super.setLinearDamping(profile.linearVelocityDamping)
        super.setAngularDamping(profile.angularDamping)
        setSimulationWorldPose(startingPose)
    }

    fun setSimulationWorldPose(robotPose: Pose2d) {
        super.transform.set(GeometryConvertor.toDyn4jTransform(robotPose))
        super.linearVelocity.set(0.0, 0.0)
    }

    /**
     * sets the robot's speeds to a given chassis speeds
     * the robot's speeds will jump to the given speeds in a tick
     * this is different from runRawChassisSpeeds(), which applies forces on the chassis and accelerates smoothly according to physics
     */
    protected fun setRobotSpeeds(givenSpeeds: ChassisSpeeds) {
        super.setLinearVelocity(GeometryConvertor.toDyn4jLinearVelocity(givenSpeeds))
        super.setAngularVelocity(givenSpeeds.omegaRadiansPerSecond)
    }

    fun simulateChassisBehaviorWithRobotRelativeSpeeds(desiredChassisSpeedsRobotRelative: ChassisSpeeds) {
        simulateChassisBehaviorWithFieldRelativeSpeeds(ChassisSpeeds.fromRobotRelativeSpeeds(desiredChassisSpeedsRobotRelative, objectOnFieldPose2d.getRotation()))
    }

    protected fun simulateChassisBehaviorWithFieldRelativeSpeeds(desiredChassisSpeedsFieldRelative: ChassisSpeeds) {
        super.setAtRest(false)

        val desiredLinearMotionPercent: Vector2 = GeometryConvertor.toDyn4jLinearVelocity(desiredChassisSpeedsFieldRelative)
            .multiply(1.0 / profile.robotMaxVelocity)
        simulateChassisTranslationalBehavior(
            Vector2.create(
                MapleCommonMath.constrainMagnitude(desiredLinearMotionPercent.magnitude, 1.0),
                desiredLinearMotionPercent.getDirection()
            )
        )

        val desiredRotationalMotionPercent: Double = desiredChassisSpeedsFieldRelative.omegaRadiansPerSecond / profile.maxAngularVelocity
        simulateChassisRotationalBehavior(MapleCommonMath.constrainMagnitude(desiredRotationalMotionPercent, 1.0))
    }

    protected fun simulateChassisTranslationalBehavior(desiredLinearMotionPercent: Vector2) {
        val robotRequestedToMoveLinearly: Boolean = desiredLinearMotionPercent.getMagnitude() > 0.03
        if (!robotRequestedToMoveLinearly) {
            simulateTranslationalFrictionNoMotion()
            return
        }
        val forceVec: Vector2 = desiredLinearMotionPercent.copy().multiply(profile.propellingForce)
        super.applyForce(Force(forceVec))
    }

    protected fun simulateTranslationalFrictionNoMotion() {
        val actualLinearPercent: Double = getLinearVelocity().getMagnitude() / profile.robotMaxVelocity
        val robotActuallyMovingLinearly: Boolean = actualLinearPercent > 0.03
        if (robotActuallyMovingLinearly) super.applyForce(
            Force(
                super.linearVelocity.getNormalized().multiply(-profile.frictionForce)
            )
        )
        else super.setLinearVelocity(Vector2())
    }

    protected fun simulateChassisRotationalBehavior(desiredRotationalMotionPercent: Double) {
        val maximumTorque: Double = profile.maxAngularAcceleration * super.getMass().getInertia()
        if (abs(desiredRotationalMotionPercent) > 0.01) {
            super.applyTorque(desiredRotationalMotionPercent * maximumTorque)
            return
        }

        val actualRotationalMotionPercent: Double = abs(getAngularVelocity() / profile.maxAngularVelocity)
        val frictionalTorqueMagnitude: Double = profile.angularFrictionAcceleration * super.getMass().getInertia()
        if (actualRotationalMotionPercent > 0.01) super.applyTorque(frictionalTorqueMagnitude.withSign(-super.getAngularVelocity()))
        else super.setAngularVelocity(0.0)
    }

    override val objectOnFieldPose2d: Pose2d
        get() {
            return GeometryConvertor.toWpilibPose2d(getTransform()).transformBy(profile.robotBumperToCenterOffset)
        }

    open val measuredChassisSpeedsRobotRelative: ChassisSpeeds
        get() = ChassisSpeeds.fromFieldRelativeSpeeds(measuredChassisSpeedsFieldRelative, objectOnFieldPose2d.getRotation())

    open val measuredChassisSpeedsFieldRelative: ChassisSpeeds
        get() {
            return GeometryConvertor.toWpilibChassisSpeeds(getLinearVelocity(), getAngularVelocity())
        }

    /**
     * called in every iteration of sub-period
     */
    abstract fun updateSimulationSubTick(iterationNum: Int, subPeriodSeconds: Double)
}
