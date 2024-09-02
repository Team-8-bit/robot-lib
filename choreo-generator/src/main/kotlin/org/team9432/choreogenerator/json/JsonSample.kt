package org.team9432.choreogenerator.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class JsonSample(
    @SerialName("x")
    val x: Double,
    @SerialName("y")
    val y: Double,
    @SerialName("heading")
    val heading: Double,
    @SerialName("angularVelocity")
    val angularVelocity: Double,
    @SerialName("velocityX")
    val velocityX: Double,
    @SerialName("velocityY")
    val velocityY: Double,
    @SerialName("moduleForcesX")
    val moduleForcesX: List<Double>,
    @SerialName("moduleForcesY")
    val moduleForcesY: List<Double>,
    @SerialName("timestamp")
    val timestamp: Double,
)