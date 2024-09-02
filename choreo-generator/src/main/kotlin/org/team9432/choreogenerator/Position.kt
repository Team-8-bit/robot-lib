package org.team9432.choreogenerator

import org.team9432.choreogenerator.json.JsonChoreoWaypoint
import org.team9432.lib.unit.*
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

class Position(x: Length, y: Length, heading: Angle = 0.degrees) {
    var x: Length = x
        private set

    var y: Length = y
        private set

    var heading: Angle = heading
        private set

    fun pointAt(other: Position): Position {
        heading = atan2(other.y.inMeters - this.y.inMeters, other.x.inMeters - this.x.inMeters).radians

        return this
    }

    fun pointAwayFrom(other: Position): Position {
        heading = atan2(other.y.inMeters - this.y.inMeters, other.x.inMeters - this.x.inMeters).radians + 180.degrees

        return this
    }

    fun interpolate(other: Position, t: Double): Position {
        x = interpolateDouble(this.x.inMeters, other.x.inMeters, t).meters
        y = interpolateDouble(this.y.inMeters, other.y.inMeters, t).meters

        return this
    }

    fun moveTowards(other: Position, distance: Length): Position {
        val percentageFromStart = distance.inMeters / distanceTo(other).inMeters
        this.interpolate(other, percentageFromStart)

        return this
    }

    fun move(x: Length = 0.meters, y: Length = 0.meters, heading: Angle = 0.degrees): Position {
        this.x += x
        this.y += y
        this.heading += heading

        return this
    }

    fun moveX(distance: Length): Position {
        x += distance
        return this
    }

    fun moveY(distance: Length): Position {
        y += distance
        return this
    }

    fun rotateBy(angle: Angle): Position {
        heading += angle
        return this
    }

    private fun interpolateDouble(startValue: Double, endValue: Double, t: Double): Double {
        return startValue + (endValue - startValue) * clamp(t, 0.0, 1.0)
    }

    private fun clamp(value: Double, low: Double, high: Double) = max(low, min(value, high))

    fun distanceTo(other: Position): Length {
        return hypot(other.x.inMeters - this.x.inMeters, other.y.inMeters - this.y.inMeters).meters
    }

    fun copy() = Position(x, y, heading)

}

internal fun JsonChoreoWaypoint.getPosition() = Position(x.meters, y.meters, heading.radians)
