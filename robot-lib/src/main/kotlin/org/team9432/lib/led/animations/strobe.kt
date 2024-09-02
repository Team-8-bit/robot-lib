package org.team9432.lib.led.animations

import kotlinx.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.Black
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import kotlin.time.Duration

/**
 * Flashes a color on and off at the given period.
 *
 * Does not end.
 *
 * @param color the color to set.
 * @param period the time of one on/off cycle.
 */
fun Section.strobe(
    color: Color,
    period: Duration,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.resetToDefault()

        var isOn = false
        while (true) {
            delay(period / 2)
            isOn = !isOn

            colorset.setProlongedColor(if (isOn) color else Color.Black)
        }
    }
}