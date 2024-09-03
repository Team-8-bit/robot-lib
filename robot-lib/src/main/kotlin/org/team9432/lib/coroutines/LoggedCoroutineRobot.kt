package org.team9432.lib.coroutines

import edu.wpi.first.wpilibj.DriverStation
import kotlinx.coroutines.launch
import org.littletonrobotics.junction.LoggedRobot
import org.team9432.lib.RobotPeriodicManager

open class LoggedCoroutineRobot: LoggedRobot(PERIOD), Team8BitRobot {
    final override val isSimulated = isSimulation()
    final override var alliance: DriverStation.Alliance? = null
        private set

    final override val mode: Team8BitRobot.Mode
        get() {
            return when {
                this.isDisabled -> Team8BitRobot.Mode.DISABLED
                this.isAutonomousEnabled -> Team8BitRobot.Mode.AUTONOMOUS
                this.isTeleopEnabled -> Team8BitRobot.Mode.TELEOP
                this.isTestEnabled -> Team8BitRobot.Mode.TEST
                else -> Team8BitRobot.Mode.NONE
            }
        }

    override fun robotPeriodic() {
        RobotCoroutineManager.updateCoroutines()
        DriverStation.getAlliance().ifPresent { alliance = it }
        RobotPeriodicManager.invokeAll()
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