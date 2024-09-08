package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import kotlinx.coroutines.CoroutineScope
import org.team9432.lib.coroutines.Team8BitRobot
import kotlin.properties.Delegates

object Library {
    private lateinit var robot: Team8BitRobot

    /** The alliance that the robot is on according to the driver station. Null if not connected. */
    internal val alliance: DriverStation.Alliance? get() = robot.alliance

    /** True if the robot is running in simulation. */
    internal val isSimulated: Boolean get() = robot.isSimulated && runtime != Team8BitRobot.Runtime.REPLAY

    /** True if the robot is running in simulation. */
    internal lateinit var runtime: Team8BitRobot.Runtime
        private set

    /** The [CoroutineScope] of the current robot. */
    internal val coroutineScope: CoroutineScope get() = robot.coroutineScope

    internal var robotPeriod: Double by Delegates.notNull()
        private set

    /** Initializes the library, some features will not work unless this is called. */
    fun initialize(robot: Team8BitRobot, runtime: Team8BitRobot.Runtime) {
        this.runtime = runtime
        this.robot = robot
        this.robotPeriod = robot.periodSeconds
    }
}