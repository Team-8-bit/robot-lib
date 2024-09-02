package org.team9432.lib.led.animations

import kotlinx.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Slides a new color in from one side.
 *
 * Ends once the section is covered.
 *
 * @param color the color being filled in.
 * @param leadColor the color of the first light on the moving part, defaults to [color].
 * @param runReversed if the animation should run in the opposite direction.
 * @param timePerStep the time between each step of the moving light.
 */
fun Section.chaseColors(
    colors: List<Color>,
    runReversed: Boolean = false,
    timePerStep: Duration = 20.milliseconds,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.setCurrentlyFadingColor(null)

        val indices = colorset.indices.toList().let { if (runReversed) it.reversed() else it }

        for (color in colors) {
            for (currentPosition in indices) {

                colorset.applyTo(currentPosition) {
                    prolongedColor = color
                }

                delay(timePerStep)
            }
        }
    }
}