package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import org.team9432.lib.coroutines.Team8BitRobot
import kotlin.properties.Delegates

object Library {
    private lateinit var robot: Team8BitRobot

    /** The alliance that the robot is on according to the driver station. Null if not connected. */
    internal val alliance: DriverStation.Alliance? get() = robot.alliance

    /** True if the robot is running in simulation. */
    internal val isSimulated: Boolean get() = robot.isSimulated

    /** Initializes the library, some features will not work unless this is called. */
    fun initialize(robot: Team8BitRobot) {
        this.robot = robot
    }
}