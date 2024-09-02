package org.team9432.choreogenerator.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*

@Serializable
internal data class JsonConstraint(
    @SerialName("scope")
    @Serializable(with = ScopeSerializer::class)
    val scope: Set<String>,
    @SerialName("type")
    val type: String,
    @SerialName("velocity")
    val velocity: Double? = null,
    @SerialName("angular_velocity")
    val angularVelocity: Double? = null,
    @SerialName("x")
    val x: Double? = null,
    @SerialName("y")
    val y: Double? = null,
    @SerialName("tolerance")
    val tolerance: Double? = null,
)

private object ScopeSerializer: JsonTransformingSerializer<Set<String>>(SetSerializer(String.serializer())) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        JsonArray((element as JsonArray).map { JsonPrimitive((it as JsonPrimitive).content) })

    override fun transformSerialize(element: JsonElement): JsonElement =
        JsonArray((element as JsonArray).map { it as JsonPrimitive }.map { scope -> scope.intOrNull?.let { JsonPrimitive(it) } ?: JsonPrimitive(scope.content) })
}