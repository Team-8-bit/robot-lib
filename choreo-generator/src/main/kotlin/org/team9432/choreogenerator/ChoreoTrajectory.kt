package org.team9432.choreogenerator

import org.team9432.choreogenerator.json.JsonPath

class ChoreoTrajectory(
    val name: String,
    includeDefaultConstraints: Boolean = true,
    private val usesControlIntervalGuessing: Boolean = true,
    private val defaultControlIntervalCount: Int = 40,
    private val usesDefaultFieldObstacles: Boolean = true,
) {
    private val waypoints = mutableListOf<ChoreoWaypoint>()
    private val constraints = mutableListOf<ChoreoConstraint>()

    init {
        if (includeDefaultConstraints) {
            addConstraint(InitialStopPoint)
            addConstraint(FinalStopPoint)
        }
    }

    fun addPoseWaypoint(position: Position, stopPoint: Boolean = false) = addWaypoint(position.asPoseWaypoint(), stopPoint)
    fun addTranslationWaypoint(position: Position, stopPoint: Boolean = false) = addWaypoint(position.asTranslationWaypoint(), stopPoint)

    private fun addWaypoint(waypoint: ChoreoWaypoint, stopPoint: Boolean): Int {
        waypoints.add(waypoint)
        val waypointIndex = waypoints.lastIndex

        if (stopPoint) {
            addConstraint(StopPoint(waypointIndex))
        }

        return waypointIndex
    }

    fun addConstraint(constraint: ChoreoConstraint) {
        constraints.add(constraint)
    }

    internal fun getJsonPath() = JsonPath(
        waypoints = waypoints.map { it.jsonWaypoint },
        samples = emptyList(),
        trajectoryWaypoints = emptyList(),
        constraints = constraints.map { it.jsonConstraint },
        usesControlIntervalGuessing = usesControlIntervalGuessing,
        defaultControlIntervalCount = defaultControlIntervalCount,
        usesDefaultFieldObstacles = usesDefaultFieldObstacles,
        circleObstacles = emptyList(),
        eventMarkers = emptyList(),
        isTrajectoryStale = true,
    )
}