package org.team9432.lib

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.networktables.PubSubOption
import edu.wpi.first.networktables.StringPublisher
import edu.wpi.first.networktables.StringTopic

object Elastic {
    private val topic: StringTopic = NetworkTableInstance.getDefault().getStringTopic("/Elastic/RobotNotifications")
    private val publisher: StringPublisher = topic.publish(PubSubOption.sendAll(true), PubSubOption.keepDuplicates(true))
    private val objectMapper = ObjectMapper()

    fun sendAlert(alert: ElasticNotification) {
        try {
            publisher.set(objectMapper.writeValueAsString(alert))
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }

    class ElasticNotification(
        @field:JsonProperty("level") var level: NotificationLevel,
        @field:JsonProperty("title") var title: String,
        @field:JsonProperty("description") var description: String,
    ) {
        enum class NotificationLevel {
            INFO,
            WARNING,
            ERROR
        }
    }
}