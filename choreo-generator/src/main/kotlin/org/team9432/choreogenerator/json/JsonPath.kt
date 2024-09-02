package org.team9432.choreogenerator.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class JsonPath(
    @SerialName("waypoints")
    val waypoints: List<JsonChoreoWaypoint>,
    @SerialName("trajectory")
    val samples: List<JsonSample>,
    @SerialName("trajectoryWaypoints")
    val trajectoryWaypoints: List<JsonTrajectoryWaypoint>,
    @SerialName("constraints")
    val constraints: List<JsonConstraint>,
    @SerialName("usesControlIntervalGuessing")
    val usesControlIntervalGuessing: Boolean,
    @SerialName("defaultControlIntervalCount")
    val defaultControlIntervalCount: Int,
    @SerialName("usesDefaultFieldObstacles")
    val usesDefaultFieldObstacles: Boolean,
    @SerialName("circleObstacles")
    val circleObstacles: List<Nothing>,
    @SerialName("eventMarkers")
    val eventMarkers: List<Nothing>,
    @SerialName("isTrajectoryStale")
    var isTrajectoryStale: Boolean,
)