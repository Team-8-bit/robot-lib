package org.team9432.choreogenerator.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class JsonChoreoFile(
    @SerialName("version")
    val version: String,
    @SerialName("robotConfiguration")
    val robotConfiguration: JsonRobotConfiguration,
    @SerialName("paths")
    var paths: Map<String, JsonPath>,
    @SerialName("splitTrajectoriesAtStopPoints")
    val splitTrajectoriesAtStopPoints: Boolean,
    @SerialName("usesObstacles")
    val usesObstacles: Boolean,
)