package org.team9432.lib.led.animations

import kotlinx.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Instantly sets the section to the specified color.
 *
 * Ends after [duration].
 *
 * @param color the color to set.
 * @param duration the time to go keep the strip at this color.
 */
fun Section.solid(
    color: Color,
    duration: Duration = Integer.MAX_VALUE.seconds,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.applyToEach {
            resetToDefault()
            prolongedColor = color
        }

        delay(duration)
    }
}