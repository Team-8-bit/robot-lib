package org.team9432.choreogenerator.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class JsonTrajectoryWaypoint(
    @SerialName("timestamp")
    val timestamp: Double,
    @SerialName("isStopPoint")
    val isStopPoint: Boolean,
    @SerialName("x")
    val x: Double,
    @SerialName("y")
    val y: Double,
    @SerialName("heading")
    val heading: Double,
    @SerialName("isInitialGuess")
    val isInitialGuess: Boolean,
    @SerialName("translationConstrained")
    val translationConstrained: Boolean,
    @SerialName("headingConstrained")
    val headingConstrained: Boolean,
    @SerialName("controlIntervalCount")
    val controlIntervalCount: Int,
)