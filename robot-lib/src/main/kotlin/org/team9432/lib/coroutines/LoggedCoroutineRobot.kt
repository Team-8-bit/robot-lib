package org.team9432.lib.coroutines

import edu.wpi.first.wpilibj.RobotBase
import kotlinx.coroutines.launch
import org.littletonrobotics.junction.LoggedRobot

open class LoggedCoroutineRobot: LoggedRobot(PERIOD) {
    val isSimulated = RobotBase.isSimulation()

    override fun robotPeriodic() {
        RobotCoroutineManager.updateCoroutines()
    }

    companion object {
        const val PERIOD = 0.02
    }

    open suspend fun init() {}
    open suspend fun disabled() {}
    open suspend fun autonomous() {}
    open suspend fun teleop() {}
    open suspend fun test() {}

    override fun robotInit() {
        RobotCoroutineManager.coroutineScope.launch { init() }
    }

    override fun disabledInit() {
        RobotCoroutineManager.coroutineScope.launch { disabled() }
    }

    override fun autonomousInit() {
        RobotCoroutineManager.coroutineScope.launch { autonomous() }
    }

    override fun teleopInit() {
        RobotCoroutineManager.coroutineScope.launch { teleop() }
    }

    override fun testInit() {
        RobotCoroutineManager.coroutineScope.launch { test() }
    }
}