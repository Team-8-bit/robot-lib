// By 6328
// https://github.com/Mechanical-Advantage/RobotCode2024/blob/a025615a52193b7709db7cf14c51c57be17826f2/src/main/java/org/littletonrobotics/frc2024/subsystems/drive/Drive.java
package org.team9432.lib.dashboard

import org.littletonrobotics.junction.networktables.LoggedDashboardNumber
import org.team9432.lib.Library
import kotlin.reflect.KProperty

/**
 * Class for a tunable number. Gets value from dashboard in tuning mode, returns default if not or
 * value not in dashboard.
 */
class LoggedTunableNumber(key: String, private val defaultValue: Double): () -> Double {
    private var dashboardNumber: LoggedDashboardNumber? = null
    private val lastHasChangedValues: MutableMap<Int, Double> = HashMap()

    init {
        if (Library.tuningMode) {
            dashboardNumber = LoggedDashboardNumber("$TABLE_KEY/$key", defaultValue)
        }
    }

    /**
     * Get the current value, from dashboard if available and in tuning mode.
     *
     * @return The current value
     */
    fun get() = if (Library.tuningMode) dashboardNumber?.get() ?: defaultValue else defaultValue

    /**
     * Checks whether the number has changed since our last check
     *
     * @param id Unique identifier for the caller to avoid conflicts when shared between multiple
     * objects. Recommended approach is to pass the result of "hashCode()"
     * @return True if the number has changed since the last time this method was called, false
     * otherwise.
     */
    fun hasChanged(id: Int): Boolean {
        val currentValue = get()
        val lastValue = lastHasChangedValues[id]
        if (lastValue == null || currentValue != lastValue) {
            lastHasChangedValues[id] = currentValue
            return true
        }
        return false
    }

    override fun invoke() = get()
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()

    companion object {
        private const val TABLE_KEY = "TunableNumbers"

        /**
         * Runs action if any of the tunableNumbers have changed
         *
         * @param id Unique identifier for the caller to avoid conflicts when shared between multiple *
         * objects. Recommended approach is to pass the result of "hashCode()"
         * @param action Callback to run when any of the tunable numbers have changed. Access tunable
         * numbers in order inputted in method
         * @param tunableNumbers All tunable numbers to check
         */
        fun ifChanged(id: Int, action: (List<Double>) -> Unit, vararg tunableNumbers: LoggedTunableNumber) {
            if (tunableNumbers.any { it.hasChanged(id) }) {
                action.invoke(tunableNumbers.map { it.get() })
            }
        }
    }
}