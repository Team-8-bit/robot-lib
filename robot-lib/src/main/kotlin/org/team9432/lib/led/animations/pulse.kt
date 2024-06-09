package org.team9432.lib.led.animations

import kotlinx.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Sends fading "pulses" of color down a section.
 *
 * Does not end.
 *
 * @param color the of color of pulses.
 * @param cooldown time between the end of one pulse and the start of another.
 * @param runReversed if the animation should run in the opposite direction.
 * @param timePerStep the time between each step.
 */
fun Section.pulse(
    color: Color,
    cooldown: Duration = 1.seconds,
    runReversed: Boolean = false,
    timePerStep: Duration = 20.milliseconds,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.resetToDefault()

        val stepsInOrder = colorset.indices.toList().let { if (runReversed) it.reversed() else it }

        while (true) {
            for (step in stepsInOrder) {
                colorset.setCurrentlyFadingColor(step, color)
                delay(timePerStep)
            }
            delay(cooldown)
        }
    }
}