package org.team9432.lib

object RobotPeriodicManager {
    private val periodics = mutableSetOf<() -> Any>()

    private val periodicsToStart = mutableSetOf<() -> Any>()
    private val periodicsToStop = mutableSetOf<() -> Any>()

    fun startPeriodic(function: RobotPeriodic.() -> Unit): RobotPeriodic {
        val periodic = RobotPeriodic(function)
        periodic.startPeriodic()
        return periodic
    }

    class RobotPeriodic(action: RobotPeriodic.() -> Unit) {
        val action = { action.invoke(this) }

        fun startPeriodic() {
            periodicsToStart.add(action)
        }

        fun stopPeriodic() {
            periodicsToStop.add(action)
        }
    }

    internal fun invokeAllAndStartNew() {
        periodics.addAll(periodicsToStart)
        periodics.removeAll(periodicsToStop)
        periodicsToStart.clear()
        periodicsToStop.clear()
        periodics.forEach { it.invoke() }
    }
}