package org.team9432.lib

object RobotPeriodicManager {
    private val periodics = mutableSetOf<() -> Any>()

    fun startPeriodic(function: RobotPeriodic.() -> Unit): RobotPeriodic {
        val periodic = RobotPeriodic(function)
        periodic.startPeriodic()
        return periodic
    }

    class RobotPeriodic(action: RobotPeriodic.() -> Unit) {
        val action = { action.invoke(this) }

        fun startPeriodic() {
            periodics.add(action)
        }

        fun stopPeriodic() {
            periodics.remove(action)
        }
    }

    internal fun invokeAll() = periodics.forEach { it.invoke() }
}