package org.team9432.choreogenerator

import org.team9432.choreogenerator.json.JsonConstraint
import org.team9432.lib.unit.Angle
import org.team9432.lib.unit.degrees
import org.team9432.lib.unit.inMeters
import org.team9432.lib.unit.inRadians

abstract class ChoreoConstraint {
    internal abstract val jsonConstraint: JsonConstraint
}

data class StopPoint(val waypoint: Int): ChoreoConstraint() {
    override val jsonConstraint = JsonConstraint(setOf(waypoint.toString()), "StopPoint")
}

data object InitialStopPoint: ChoreoConstraint() {
    override val jsonConstraint = JsonConstraint(setOf("first"), "StopPoint")
}

data object FinalStopPoint: ChoreoConstraint() {
    override val jsonConstraint = JsonConstraint(setOf("last"), "StopPoint")
}

data class WholePathMaxVeloticy(val velocity: Double): ChoreoConstraint() {
    override val jsonConstraint = JsonConstraint(setOf("first", "last"), "MaxVelocity", velocity = velocity)
}

data class StraightLine(val startWaypoint: Int, val endWaypoint: Int = startWaypoint): ChoreoConstraint() {
    override val jsonConstraint = JsonConstraint(setOf(startWaypoint.toString(), endWaypoint.toString()), "StraightLine")
}

data class MaxVelocity(val startWaypoint: Int, val endWaypoint: Int = startWaypoint, val velocity: Double): ChoreoConstraint() {
    override val jsonConstraint = JsonConstraint(setOf(startWaypoint.toString(), endWaypoint.toString()), "MaxVelocity", velocity = velocity)
}

data class MaxAngVelocity(val startWaypoint: Int, val endWaypoint: Int = startWaypoint, val angularVelocity: Double): ChoreoConstraint() {
    override val jsonConstraint = JsonConstraint(setOf(startWaypoint.toString(), endWaypoint.toString()), "MaxAngularVelocity", velocity = angularVelocity)
}

data class PointAt(val startWaypoint: Int, val endWaypoint: Int = startWaypoint, val target: Position, val headingTolerance: Angle = 0.degrees): ChoreoConstraint() {
    override val jsonConstraint = JsonConstraint(setOf(startWaypoint.toString(), endWaypoint.toString()), "PointAt", x = target.x.inMeters, y = target.y.inMeters, tolerance = headingTolerance.inRadians)
}